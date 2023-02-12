package io.github.zrdzn.web.chattee.backend.user.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.zrdzn.web.chattee.backend.user.User;
import io.github.zrdzn.web.chattee.backend.user.UserFacade;
import io.github.zrdzn.web.chattee.backend.user.session.Session;
import io.github.zrdzn.web.chattee.backend.user.session.SessionFacade;
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

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.accepted;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.unauthorized;

@Endpoints
public class AuthEndpoints {

    public static final String ENDPOINT = "/api/v1/auth";

    private final UserFacade userFacade;
    private final SessionFacade sessionFacade;

    public AuthEndpoints(UserFacade userFacade, SessionFacade sessionFacade) {
        this.userFacade = userFacade;
        this.sessionFacade = sessionFacade;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create an authorization token.",
            description = "Creates an authorization token and returns it.",
            tags = { "Auth" },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = AuthCredentials.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "202",
                            description = "Token that is used for authorizations.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when user with provided email does not exist.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message when password is incorrect or something went wrong with creating token.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ENDPOINT)
    public void authenticate(Context context) {
        context.async(() ->
                bodyAsClass(context, AuthCredentials.class, "Authentication body is empty or invalid.")
                        .filter(credentials -> credentials.getEmail() != null, ignored -> badRequest("'email' must not be null."))
                        .filter(credentials -> credentials.getPassword() != null, ignored -> badRequest("'password' must not be null."))
                        .flatMap(user -> {
                            Result<Optional<User>, HttpResponse> userResult = this.userFacade.getUser(user.getEmail());
                            if (userResult.isErr()) {
                                return Result.error(badRequest("User with provided email does not exist."));
                            }

                            Optional<User> userMaybe = userResult.get();
                            if (userMaybe.isEmpty()) {
                                return Result.error(badRequest("User with provided email does not exist."));
                            }

                            User foundUser = userMaybe.get();

                            if (!BCrypt.verifyer().verify(user.getPassword().toCharArray(), foundUser.getPassword().toCharArray()).verified) {
                                return Result.error(unauthorized("Provided password is invalid."));
                            }

                            Session session = new Session(foundUser.getId(), Instant.now().plus(7, ChronoUnit.DAYS), context.ip());

                            Result<Session, HttpResponse> createSessionResult = this.sessionFacade.createSession(session);
                            if (createSessionResult.isErr()) {
                                return Result.error(unauthorized("Something went wrong while creating a session."));
                            }

                            return createSessionResult;
                        }).peek(session -> context.status(HttpStatus.ACCEPTED).json(accepted(session.getToken())))
                        .onError(error -> context.status(error.code()).json(error)));
    }

}
