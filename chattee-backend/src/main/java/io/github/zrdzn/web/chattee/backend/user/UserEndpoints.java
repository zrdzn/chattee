package io.github.zrdzn.web.chattee.backend.user;

import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.javalin.community.routing.annotations.Delete;
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

public class UserEndpoints {

    public static final String USER_ENDPOINT = "/api/v1/users";
    public static final String PRIVILEGE_ENDPOINT = "/api/v1/users/{userId}/privileges";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final UserFacade userFacade;

    public UserEndpoints(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @OpenApi(
            path = USER_ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create an user.",
            description = "Creates an user and returns it.",
            tags = { "User" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = User.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created user.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(USER_ENDPOINT)
    public void createUser(Context context) {
        context.async(() ->
                bodyAsClass(context, User.class, "User body is empty or invalid.")
                        .filter(user -> user.getEmail() != null, ignored -> badRequest("'email' must not be null."))
                        .filter(user -> validateEmail(user.getEmail()), ignored -> badRequest("'email' is not a valid email."))
                        .filter(user -> user.getEmail().length() < 65, ignored -> badRequest("'email' must be shorter than 65 characters."))
                        .filter(user -> user.getPassword() != null, ignored -> badRequest("'password' must not be null."))
                        .filter(user -> user.getPassword().length() > 6, ignored -> badRequest("'password' must be longer than 6 characters."))
                        .filter(user -> user.getPassword().length() < 81, ignored -> badRequest("'password' must be shorter than 81 characters."))
                        .filter(user -> user.getUsername() != null, ignored -> badRequest("'username' must not be null."))
                        .filter(user -> user.getUsername().length() > 3, ignored -> badRequest("'username' must be longer than 3 characters."))
                        .filter(user -> user.getUsername().length() < 33, ignored -> badRequest("'username' must be shorter than 33 characters."))
                        .flatMap(this.userFacade::createUser)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("User has been created.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a privilege.",
            description = "Creates a privilege and returns it.",
            tags = { "User Privilege" },
            pathParams = {
                    @OpenApiParam(name = "userId", description = "A numeric identifier associated with a record in the database.", required = true)
            },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = UserPrivilege.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created privilege.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(PRIVILEGE_ENDPOINT)
    public void createPrivilege(Context context) {
        context.async(() ->
                bodyAsClass(context, UserPrivilege.class, "Privilege body is empty or invalid.")
                        .filter(privilege -> privilege.getPrivilege() != null, ignored -> badRequest("'privilege' must not be null."))
                        .flatMap(privilege -> Result.supplyThrowing(() -> {
                            privilege.setUserId(context.pathParamAsClass("userId", Long.class).get());
                            return privilege;
                        }).mapErr(error -> badRequest("'userId' is not a valid number type.")))
                        .flatMap(this.userFacade::createPrivilege)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("Privilege has been created.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = USER_ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get all users.",
            description = "Returns all users.",
            tags = { "User" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted users.",
                            content = { @OpenApiContent(from = User[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(USER_ENDPOINT)
    public void getAllUsers(Context context) {
        context.async(() ->
                this.userFacade.getAllUsers()
                        .peek(users -> context.status(HttpStatus.OK).json(users))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get users privileges.",
            description = "Returns users privileges.",
            tags = { "User Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(name = "userId", description = "A numeric identifier associated with a record in the database.", required = true)
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted users privileges.",
                            content = { @OpenApiContent(from = UserPrivilege[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(PRIVILEGE_ENDPOINT)
    public void getAllPrivileges(Context context) {
        context.async(() ->
                this.userFacade.getAllPrivileges()
                        .peek(privileges -> context.status(HttpStatus.OK).json(privileges))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = USER_ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get an user.",
            description = "Returns an user.",
            tags = { "User" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted user.",
                            content = { @OpenApiContent(from = User.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Error message related to empty result based on your request.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(USER_ENDPOINT + "/{id}")
    public void getUser(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.userFacade::getUser)
                        .peek(userMaybe -> userMaybe.ifPresentOrElse(user -> context.status(HttpStatus.OK).json(user),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find this user."))))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get a privilege.",
            description = "Returns a privilege.",
            tags = { "User Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(name = "userId", description = "A numeric identifier associated with a record in the database.", required = true),
                    @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true)
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted user privilege.",
                            content = { @OpenApiContent(from = UserPrivilege.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Error message related to empty result based on your request.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(PRIVILEGE_ENDPOINT + "/{id}")
    public void getPrivilege(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.userFacade::getPrivilege)
                        .peek(privilegeMaybe -> privilegeMaybe.ifPresentOrElse(privilege -> context.status(HttpStatus.OK).json(privilege),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find a privilege."))))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = USER_ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove an user.",
            description = "Removes an user.",
            tags = { "User" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Removed user.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(USER_ENDPOINT + "/{id}")
    public void removeUser(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.userFacade::removeUser)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("User has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove an user privilege.",
            description = "Removes an user privilege.",
            tags = { "User Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = {
                    @OpenApiParam(name = "userId", description = "A numeric identifier associated with a record in the database.", required = true),
                    @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true)
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Removed user privilege.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(PRIVILEGE_ENDPOINT + "/{id}")
    public void removePrivilege(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.userFacade::removePrivilege)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Privilege has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    private static boolean validateEmail(String rawEmail) {
        return EMAIL_PATTERN.matcher(rawEmail).find();
    }

}
