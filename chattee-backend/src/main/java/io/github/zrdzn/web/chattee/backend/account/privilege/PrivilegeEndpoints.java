package io.github.zrdzn.web.chattee.backend.account.privilege;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiResponse;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;

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
            path = PRIVILEGE_ENDPOINT + "/{accountId}",
            methods = { HttpMethod.GET },
            summary = "Get privileges",
            description = "Returns privileges",
            tags = { "Account Privilege" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
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
                .peek(authDetails -> pathParamAsLong(context, "accountId", "Specified identifier is not a valid long number.")
                        .flatMap(this.privilegeService::getPrivilegesByAccountId)
                        .peek(privileges -> context.status(HttpStatus.OK).json(privileges))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

}
