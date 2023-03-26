package io.github.zrdzn.web.chattee.backend.account;

import java.util.regex.Pattern;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.community.routing.annotations.Delete;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.javalin.community.routing.annotations.Post;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import panda.utilities.StringUtils;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class AccountEndpoints {

    public static final String ACCOUNT_ENDPOINT = "/api/v1/accounts";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final AccountService accountService;
    private final AuthService authService;

    public AccountEndpoints(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    private static boolean validateEmail(String rawEmail) {
        return EMAIL_PATTERN.matcher(rawEmail).find();
    }

    @OpenApi(
            path = ACCOUNT_ENDPOINT + "/register",
            methods = { HttpMethod.POST },
            summary = "Registers an account",
            description = "Registers an account and returns it",
            tags = { "Account" },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = AccountCreateRequest.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Registered account",
                            content = { @OpenApiContent(from = Account.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ACCOUNT_ENDPOINT + "/register")
    public void registerAccount(Context context) {
        bodyAsClass(context, AccountCreateRequest.class, "Account body is empty or invalid.")
                .filter(account -> account.getEmail() != null, ignored -> badRequest("'email' must not be null."))
                .filter(account -> validateEmail(account.getEmail()), ignored -> badRequest("'email' is not a valid email."))
                .filter(account -> account.getEmail().length() < 65, ignored -> badRequest("'email' must be shorter than 65 characters."))
                .filterNot(account -> StringUtils.isEmpty(account.getPassword()), ignored -> badRequest("'rawPassword' must not be empty."))
                .filter(account -> account.getPassword().length() > 6, ignored -> badRequest("'rawPassword' must be longer than 6 characters."))
                .filter(account -> account.getPassword().length() < 81, ignored -> badRequest("'rawPassword' must be shorter than 81 characters."))
                .filterNot(account -> StringUtils.isEmpty(account.getUsername()), ignored -> badRequest("'username' must not be null."))
                .filter(account -> account.getUsername().length() > 3, ignored -> badRequest("'username' must be longer than 3 characters."))
                .filter(account -> account.getUsername().length() < 33, ignored -> badRequest("'username' must be shorter than 33 characters."))
                .flatMap(this.accountService::registerAccount)
                .peek(accountDetails -> context.status(HttpStatus.CREATED).json(accountDetails))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ACCOUNT_ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get all accounts",
            description = "Returns all accounts",
            tags = { "Account" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted accounts",
                            content = { @OpenApiContent(from = Account[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ACCOUNT_ENDPOINT)
    public void getAllAccounts(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.ACCOUNT_VIEW_ALL)
                .peek(authDetails -> this.accountService.getAllAccounts()
                        .peek(accounts -> context.status(HttpStatus.OK).json(accounts))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ACCOUNT_ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get an account",
            description = "Returns an account",
            tags = { "Account" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            pathParams = @OpenApiParam(
                    name = "id",
                    description = "A numeric identifier associated with a record in the database",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted account",
                            content = { @OpenApiContent(from = Account.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Error message related to empty result based on your request",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ACCOUNT_ENDPOINT + "/{id}")
    public void getAccount(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.ACCOUNT_VIEW)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountService::getAccount)
                        .peek(accountDetails -> context.status(HttpStatus.OK).json(accountDetails))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ACCOUNT_ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove an account",
            description = "Removes an account",
            tags = { "Account" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            pathParams = @OpenApiParam(
                    name = "id",
                    description = "A numeric identifier associated with a record in the database",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Removed account",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(ACCOUNT_ENDPOINT + "/{id}")
    public void removeAccount(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.ACCOUNT_DELETE)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountService::removeAccount)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Account has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

}
