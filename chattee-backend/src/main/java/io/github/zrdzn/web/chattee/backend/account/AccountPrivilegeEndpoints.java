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

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.created;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class AccountPrivilegeEndpoints {

    public static final String PRIVILEGE_ENDPOINT = "/api/v1/privileges";

    private final AccountService accountService;

    public AccountPrivilegeEndpoints(AccountService accountService) {
        this.accountService = accountService;
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a privilege",
            description = "Creates a privilege and returns it",
            tags = { "Account Privilege" },
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
                        .filter(privilege -> privilege.getAccountId() > 0L, ignored -> badRequest("'accountId' must be higher than 0."))
                        .filter(privilege -> privilege.getPrivilege() != null, ignored -> badRequest("'privilege' must not be null."))
                        .flatMap(this.accountService::createPrivilege)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("Privilege has been created.")))
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
                this.accountService.getAllPrivileges()
                        .peek(privileges -> context.status(HttpStatus.OK).json(privileges))
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
                        .flatMap(this.accountService::getPrivilege)
                        .peek(privilegeMaybe -> privilegeMaybe.ifPresentOrElse(privilege -> context.status(HttpStatus.OK).json(privilege),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find a privilege."))))
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
                        .flatMap(this.accountService::removePrivilege)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Privilege has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

}
