package io.github.zrdzn.web.chattee.backend.account.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.session.Session;
import io.github.zrdzn.web.chattee.backend.account.session.SessionService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Post;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import panda.std.Result;
import panda.utilities.StringUtils;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.accepted;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.unauthorized;

@Endpoints
public class AuthEndpoints {

    public static final String ENDPOINT = "/api/v1/auth";

    private final AccountService accountService;
    private final SessionService sessionService;

    public AuthEndpoints(AccountService accountService, SessionService sessionService) {
        this.accountService = accountService;
        this.sessionService = sessionService;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create an authorization token",
            description = "Creates an authorization token and returns it",
            tags = { "Auth" },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = AuthCredentials.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "202",
                            description = "Token that is used for authorizations",
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
                .flatMap(credentials -> {
                    Result<Account, HttpResponse> accountMaybe = this.accountService.getAccount(credentials.getEmail());
                    if (accountMaybe.isErr()) {
                        return Result.error(accountMaybe.getError());
                    }

                    Account account = accountMaybe.get();

                    if (!BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), account.getPassword().toCharArray()).verified) {
                        return Result.error(unauthorized("Provided password is invalid."));
                    }

                    Session session = new Session(account.getId(), Instant.now().plus(7, ChronoUnit.DAYS), context.ip());

                    Result<Session, HttpResponse> createSessionResult = this.sessionService.createSession(session);
                    if (createSessionResult.isErr()) {
                        return Result.error(unauthorized(createSessionResult.getError().message()));
                    }

                    return createSessionResult;
                }).peek(session -> context.status(HttpStatus.ACCEPTED)
                        .json(accepted(session.getToken()))
                        .sessionAttribute("tokenid", session.getToken()))
                .onError(error -> context.status(error.code()).json(error));
    }

}
