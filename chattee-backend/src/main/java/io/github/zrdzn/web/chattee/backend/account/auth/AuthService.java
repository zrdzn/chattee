package io.github.zrdzn.web.chattee.backend.account.auth;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.account.privilege.Privilege;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeService;
import io.github.zrdzn.web.chattee.backend.account.session.Session;
import io.github.zrdzn.web.chattee.backend.account.session.SessionService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.http.Context;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.extractTokenFromContext;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.forbidden;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.unauthorized;

public class AuthService {

    private final SessionService sessionService;
    private final PrivilegeService privilegeService;

    public AuthService(SessionService sessionService, PrivilegeService privilegeService) {
        this.sessionService = sessionService;
        this.privilegeService = privilegeService;
    }

    public Result<Session, HttpResponse> authorizeFor(Context context, RoutePrivilege... routeRoutePrivileges) {
        Optional<String> token = extractTokenFromContext(context);

        if (token.isEmpty()) {
            return Result.error(unauthorized("You must provide an access token."));
        }

        Result<Session, HttpResponse> sessionMaybe = this.sessionService.getSession(token.get());
        if (sessionMaybe.isErr()) {
            return Result.error(unauthorized(sessionMaybe.getError().message()));
        }

        Session session = sessionMaybe.get();

        if (routeRoutePrivileges.length > 0) {
            Result<List<Privilege>, HttpResponse> privilegesResult = this.privilegeService.getPrivilegesByAccountId(session.getAccountId());
            if (privilegesResult.isErr()) {
                return Result.error(forbidden(privilegesResult.getError().message()));
            }

            boolean hasAccess = false;
            for (Privilege privilege : privilegesResult.get()) {
                for (RoutePrivilege routePrivilege : routeRoutePrivileges) {
                    if (privilege.getPrivilege() == routePrivilege) {
                        hasAccess = true;
                        break;
                    }
                }
            }

            if (!hasAccess) {
                return Result.error(forbidden("You do not have access for this."));
            }
        }

        return Result.ok(session);
    }

}
