package io.github.zrdzn.web.chattee.backend.account.auth;

import io.github.zrdzn.web.chattee.backend.account.AccountFacade;
import io.github.zrdzn.web.chattee.backend.account.session.SessionFacade;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AuthWebConfig implements WebConfig {

    private final AccountFacade accountFacade;
    private final SessionFacade sessionFacade;

    public AuthWebConfig(AccountFacade accountFacade, SessionFacade sessionFacade) {
        this.accountFacade = accountFacade;
        this.sessionFacade = sessionFacade;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AuthEndpoints authEndpoints = new AuthEndpoints(this.accountFacade, this.sessionFacade);

        plugin.registerEndpoints(authEndpoints);
    }

}
