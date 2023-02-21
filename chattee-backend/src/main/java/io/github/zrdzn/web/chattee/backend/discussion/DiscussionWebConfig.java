package io.github.zrdzn.web.chattee.backend.discussion;

import io.github.zrdzn.web.chattee.backend.discussion.infrastructure.PostgresDiscussionRepository;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class DiscussionWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;

    public DiscussionWebConfig(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        DiscussionRepository discussionRepository = new PostgresDiscussionRepository(this.postgresStorage);
        DiscussionService discussionService = new DiscussionService(discussionRepository);
        DiscussionEndpoints discussionEndpoints = new DiscussionEndpoints(discussionService);

        plugin.registerEndpoints(discussionEndpoints);
    }

}
