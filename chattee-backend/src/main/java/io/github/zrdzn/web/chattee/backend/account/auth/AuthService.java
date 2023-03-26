package io.github.zrdzn.web.chattee.backend.account.auth;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetails;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsCreateRequest;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsService;
import io.github.zrdzn.web.chattee.backend.account.privilege.Privilege;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.http.Context;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.extractTokenFromContext;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.forbidden;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.unauthorized;

public class AuthService {

    private final AuthDetailsService authDetailsService;
    private final PrivilegeService privilegeService;

    public AuthService(AuthDetailsService authDetailsService, PrivilegeService privilegeService) {
        this.authDetailsService = authDetailsService;
        this.privilegeService = privilegeService;
    }

    public Result<String, HttpResponse> authenticate(Context context, AuthDetailsCreateRequest authDetailsCreateRequest) {
        return this.authDetailsService.createAuthDetails(authDetailsCreateRequest)
                .map(AuthDetails::getToken)
                .peek(token -> context.sessionAttribute("tokenid", token));
    }

    public Result<AuthDetails, HttpResponse> authorizeFor(Context context, RoutePrivilege... routePrivileges) {
        Optional<String> token = extractTokenFromContext(context);
        if (token.isEmpty()) {
            return Result.error(unauthorized("You must provide an access token."));
        }

        Result<AuthDetails, HttpResponse> authDetailsResult = this.authDetailsService.getAuthDetailsByToken(token.get());
        if (authDetailsResult.isErr()) {
            return Result.error(unauthorized(authDetailsResult.getError().message()));
        }

        AuthDetails authDetails = authDetailsResult.get();

        if (routePrivileges.length > 0) {
            Result<List<Privilege>, HttpResponse> privilegesResult = this.privilegeService.getPrivilegesByAccountId(authDetails.getAccountId());
            if (privilegesResult.isErr()) {
                return Result.error(forbidden(privilegesResult.getError().message()));
            }

            boolean hasAccess = false;
            for (RoutePrivilege routePrivilege : routePrivileges) {
                if (privilegesResult.get().stream().anyMatch(privilege ->
                        privilege.getPrivilege() == routePrivilege || privilege.getPrivilege() == RoutePrivilege.ADMINISTRATOR)) {
                    hasAccess = true;
                    break;
                }
            }

            if (!hasAccess) {
                return Result.error(forbidden("You do not have access for this."));
            }
        }

        return Result.ok(authDetails);
    }

    public Result<Blank, HttpResponse> invalidate(Context context, String token) {
        return this.authDetailsService.removeAuthDetails(token)
                .peek(blank -> context.req().getSession().invalidate());
    }

}
