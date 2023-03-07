package io.github.zrdzn.web.chattee.backend.account.privilege;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
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

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.created;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class PrivilegeEndpoints {

    public static final String PRIVILEGE_ENDPOINT = "/api/v1/privileges";

    private final PrivilegeService privilegeService;
    private final AccountService accountService;
    private final AuthService authService;

    public PrivilegeEndpoints(PrivilegeService privilegeService, AccountService accountService, AuthService authService) {
        this.privilegeService = privilegeService;
        this.accountService = accountService;
        this.authService = authService;
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
                            description = "Authorization token",
                            required = true,
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = Privilege.class)
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
                bodyAsClass(context, Privilege.class, "Privilege body is empty or invalid.")
                        .filter(privilege -> privilege.getAccountId() > 0L, ignored -> badRequest("'accountId' must be higher than 0."))
                        .filterNot(privilege -> privilege.getPrivilege() == null, ignored -> badRequest("'privilege' must not be null."))
                        .flatMap(this.accountService::createPrivilege)
                        .peek(ignored -> context.status(HttpStatus.CREATED).json(created("Privilege has been created.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = PRIVILEGE_ENDPOINT + "/{accountId}",
            methods = { HttpMethod.GET },
            summary = "Get privileges",
            description = "Returns privileges",
            tags = { "Account Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            required = true,
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
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
                            content = { @OpenApiContent(from = Privilege[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(PRIVILEGE_ENDPOINT + "/{accountId}")
    public void getAllPrivilegesByAccountId(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.ACCOUNT_PRIVILEGE_VIEW_ALL)
                .peek(session -> pathParamAsLong(context, "accountId", "Specified identifier is not a valid long number.")
                        .flatMap(this.privilegeService::getPrivilegesByAccountId)
                        .peek(privileges -> context.status(HttpStatus.OK).json(privileges))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
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
                            description = "Authorization token",
                            required = true,
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
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
