package io.github.zrdzn.web.chattee.backend.account.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetails;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsCreateDto;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
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
import panda.std.Result;
import panda.utilities.StringUtils;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.unauthorized;

@Endpoints
public class AuthEndpoints {

    public static final String ENDPOINT = "/api/v1/auth";

    private final AccountService accountService;
    private final AuthService authService;

    public AuthEndpoints(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a session",
            description = "Creates a session",
            tags = { "Auth" },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = AuthCredentials.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "204",
                            description = "Successfully set token in session's attributes",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when account with provided email does not exist",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message when password is incorrect or something went wrong with creating token",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ENDPOINT)
    public void authenticate(Context context) {
        bodyAsClass(context, AuthCredentials.class, "Authentication body is empty or invalid.")
                .filterNot(credentials -> StringUtils.isEmpty(credentials.getEmail()), ignored -> badRequest("'email' must not be empty."))
                .filterNot(credentials -> StringUtils.isEmpty(credentials.getPassword()), ignored -> badRequest("'password' must not be empty."))
                .map(credentials -> {
                    Result<Account, HttpResponse> accountMaybe = this.accountService.getAccount(credentials.getEmail());
                    if (accountMaybe.isErr()) {
                        return Result.error(accountMaybe.getError());
                    }

                    Account account = accountMaybe.get();

                    if (!BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), account.getPassword().toCharArray()).verified) {
                        return Result.error(unauthorized("Provided password is invalid."));
                    }

                    return new AuthDetailsCreateDto(account.getId(), Instant.now().plus(7, ChronoUnit.DAYS), context.ip());
                }).map(authDetailsCreateDto -> this.authService.authenticate(context, (AuthDetailsCreateDto) authDetailsCreateDto))
                .peek(authenticateResult -> context.status(HttpStatus.NO_CONTENT))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/me",
            methods = { HttpMethod.GET },
            summary = "Get session details",
            description = "Returns all session details",
            tags = { "Auth" },
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
                            description = "Resulted session details",
                            content = { @OpenApiContent(from = AuthDetails.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT + "/me")
    public void getSessionDetails(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.AUTH_DETAILS_VIEW)
                .peek(authDetails -> context.status(HttpStatus.OK).json(authDetails))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/invalidate",
            methods = { HttpMethod.POST },
            summary = "Invalidate session",
            description = "Invalidates session",
            tags = { "Auth" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "204",
                            description = "Session is successfully invalidated",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ENDPOINT + "/invalidate")
    public void invalidateSession(Context context) {
        this.authService.authorizeFor(context)
                .flatMap(authDetails -> this.authService.invalidate(context, authDetails.getToken()))
                .peek(blank -> context.status(HttpStatus.NO_CONTENT))
                .onError(error -> context.status(error.code()).json(error));
    }

}
