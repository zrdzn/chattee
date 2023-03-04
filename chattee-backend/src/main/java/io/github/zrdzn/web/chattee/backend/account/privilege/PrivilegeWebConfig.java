package io.github.zrdzn.web.chattee.backend.account.privilege;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class PrivilegeWebConfig implements WebConfig {

    private final PrivilegeService privilegeService;
    private final AccountService accountService;
    private final AuthService authService;

    public PrivilegeWebConfig(PrivilegeService privilegeService, AccountService accountService, AuthService authService) {
        this.privilegeService = privilegeService;
        this.accountService = accountService;
        this.authService = authService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        PrivilegeEndpoints privilegeEndpoints = new PrivilegeEndpoints(this.privilegeService, this.accountService, this.authService);

        plugin.registerEndpoints(privilegeEndpoints);
    }

}
