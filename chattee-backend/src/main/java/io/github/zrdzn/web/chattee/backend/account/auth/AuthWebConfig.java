package io.github.zrdzn.web.chattee.backend.account.auth;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.session.SessionService;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AuthWebConfig implements WebConfig {

    private final AccountService accountService;
    private final SessionService sessionService;
    private final AuthService authService;

    public AuthWebConfig(AccountService accountService, SessionService sessionService, AuthService authService) {
        this.accountService = accountService;
        this.sessionService = sessionService;
        this.authService = authService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AuthEndpoints authEndpoints = new AuthEndpoints(this.accountService, this.sessionService, this.authService);

        plugin.registerEndpoints(authEndpoints);
    }

}
