package io.github.zrdzn.web.chattee.backend.account.auth;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AuthWebConfig implements WebConfig {

    private final AccountService accountService;
    private final AuthService authService;

    public AuthWebConfig(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AuthEndpoints authEndpoints = new AuthEndpoints(this.accountService, this.authService);

        plugin.registerEndpoints(authEndpoints);
    }

}
