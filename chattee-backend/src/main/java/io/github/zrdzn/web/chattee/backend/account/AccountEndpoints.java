package io.github.zrdzn.web.chattee.backend.account;

import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
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
import java.util.regex.Pattern;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.created;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class AccountEndpoints {

    public static final String ACCOUNT_ENDPOINT = "/api/v1/accounts";
    public static final String PRIVILEGE_ENDPOINT = "/api/v1/accounts/{accountId}/privileges";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final AccountFacade accountFacade;

    public AccountEndpoints(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @OpenApi(
            path = ACCOUNT_ENDPOINT + "/register",
            methods = { HttpMethod.POST },
            summary = "Registers an account",
            description = "Registers an account and returns it",
            tags = { "Account" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = Account.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Registered account",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ACCOUNT_ENDPOINT + "/register")
    public void registerAccount(Context context) {
        context.async(() ->
                bodyAsClass(context, AccountRegisterDto.class, "Account body is empty or invalid.")
                        .filter(account -> account.getEmail() != null, ignored -> badRequest("'email' must not be null."))
                        .filter(account -> validateEmail(account.getEmail()), ignored -> badRequest("'email' is not a valid email."))
                        .filter(account -> account.getEmail().length() < 65, ignored -> badRequest("'email' must be shorter than 65 characters."))
                        .filter(account -> account.getRawPassword() != null, ignored -> badRequest("'rawPassword' must not be null."))
                        .filter(account -> account.getRawPassword().length() > 6, ignored -> badRequest("'rawPassword' must be longer than 6 characters."))
                        .filter(account -> account.getRawPassword().length() < 81, ignored -> badRequest("'rawPassword' must be shorter than 81 characters."))
                        .filter(account -> account.getUsername() != null, ignored -> badRequest("'username' must not be null."))
                        .filter(account -> account.getUsername().length() > 3, ignored -> badRequest("'username' must be longer than 3 characters."))
                        .filter(account -> account.getUsername().length() < 33, ignored -> badRequest("'username' must be shorter than 33 characters."))
                        .flatMap(this.accountFacade::registerAccount)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("Account has been registered.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a privilege",
            description = "Creates a privilege and returns it",
            tags = { "Account Privilege" },
            pathParams = {
                    @OpenApiParam(
                            name = "accountId",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    )
            },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = AccountPrivilege.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created privilege",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(PRIVILEGE_ENDPOINT)
    public void createPrivilege(Context context) {
        context.async(() ->
                bodyAsClass(context, AccountPrivilege.class, "Privilege body is empty or invalid.")
                        .filter(privilege -> privilege.getPrivilege() != null, ignored -> badRequest("'privilege' must not be null."))
                        .flatMap(privilege -> Result.supplyThrowing(() -> {
                            privilege.setAccountId(context.pathParamAsClass("accountId", Long.class).get());
                            return privilege;
                        }).mapErr(error -> badRequest("'accountId' is not a valid number type.")))
                        .flatMap(this.accountFacade::createPrivilege)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("Privilege has been created.")))
                        .onError(error -> context.status(error.code()).json(error)));
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
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
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
        context.async(() ->
                this.accountFacade.getAllAccounts()
                        .peek(accounts -> context.status(HttpStatus.OK).json(accounts))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get privileges",
            description = "Returns privileges",
            tags = { "Account Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(
                            name = "accountId",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted accounts privileges",
                            content = { @OpenApiContent(from = AccountPrivilege[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(PRIVILEGE_ENDPOINT)
    public void getAllPrivileges(Context context) {
        context.async(() ->
                this.accountFacade.getAllPrivileges()
                        .peek(privileges -> context.status(HttpStatus.OK).json(privileges))
                        .onError(error -> context.status(error.code()).json(error)));
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
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
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
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountFacade::getAccount)
                        .peek(accountMaybe -> accountMaybe.ifPresentOrElse(account -> context.status(HttpStatus.OK).json(account),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find this account."))))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get a privilege",
            description = "Returns a privilege",
            tags = { "Account Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(
                            name = "accountId",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    ),
                    @OpenApiParam(
                            name = "id",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted account privilege",
                            content = { @OpenApiContent(from = AccountPrivilege.class) }
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
    @Get(PRIVILEGE_ENDPOINT + "/{id}")
    public void getPrivilege(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountFacade::getPrivilege)
                        .peek(privilegeMaybe -> privilegeMaybe.ifPresentOrElse(privilege -> context.status(HttpStatus.OK).json(privilege),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find a privilege."))))
                        .onError(error -> context.status(error.code()).json(error)));
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
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
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
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountFacade::removeAccount)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Account has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove a privilege",
            description = "Removes a privilege",
            tags = { "Account Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(
                            name = "accountId",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    ),
                    @OpenApiParam(
                            name = "id",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Removed account privilege.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(PRIVILEGE_ENDPOINT + "/{id}")
    public void removePrivilege(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.accountFacade::removePrivilege)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Privilege has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    private static boolean validateEmail(String rawEmail) {
        return EMAIL_PATTERN.matcher(rawEmail).find();
    }

}
