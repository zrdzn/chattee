package io.github.zrdzn.web.chattee.backend.discussion.post;

import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.discussion.DiscussionService;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class PostWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;
    private final AccountService accountService;
    private final AuthService authService;
    private final DiscussionService discussionService;

    private PostService postService;

    public PostWebConfig(PostgresStorage postgresStorage, AccountService accountService, AuthService authService,
                         DiscussionService discussionService) {
        this.postgresStorage = postgresStorage;
        this.accountService = accountService;
        this.authService = authService;
        this.discussionService = discussionService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        PostRepository postRepository = new PostgresPostRepository(this.postgresStorage);
        this.postService = new PostService(postRepository, this.accountService, this.discussionService);
        PostEndpoints postEndpoints = new PostEndpoints(this.postService, this.authService);

        plugin.registerEndpoints(postEndpoints);
    }

    public PostService getPostService() {
        return this.postService;
    }

}
