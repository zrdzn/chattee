package io.github.zrdzn.web.chattee.backend.discussion;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class DiscussionWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;
    private final AccountService accountService;
    private final AuthService authService;

    public DiscussionWebConfig(PostgresStorage postgresStorage, AccountService accountService, AuthService authService) {
        this.postgresStorage = postgresStorage;
        this.accountService = accountService;
        this.authService = authService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        DiscussionRepository discussionRepository = new PostgresDiscussionRepository(this.postgresStorage);
        DiscussionService discussionService = new DiscussionService(discussionRepository, this.accountService);
        DiscussionEndpoints discussionEndpoints = new DiscussionEndpoints(discussionService, this.authService);

        plugin.registerEndpoints(discussionEndpoints);
    }

}
