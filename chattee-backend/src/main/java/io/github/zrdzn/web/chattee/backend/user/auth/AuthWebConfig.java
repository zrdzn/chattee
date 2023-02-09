package io.github.zrdzn.web.chattee.backend.user.auth;

import io.github.zrdzn.web.chattee.backend.user.UserFacade;
import io.github.zrdzn.web.chattee.backend.user.session.SessionFacade;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class AuthWebConfig implements WebConfig {

    private final UserFacade userFacade;
    private final SessionFacade sessionFacade;

    public AuthWebConfig(UserFacade userFacade, SessionFacade sessionFacade) {
        this.userFacade = userFacade;
        this.sessionFacade = sessionFacade;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        AuthEndpoints authEndpoints = new AuthEndpoints(this.userFacade, this.sessionFacade);

        plugin.registerEndpoints(authEndpoints);
    }

}
