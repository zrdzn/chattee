package io.github.zrdzn.web.chattee.backend.discussion;

import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.discussion.post.PostService;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class DiscussionWebConfig implements WebConfig {

    private final DiscussionService discussionService;
    private final AuthService authService;
    private final PostService postService;

    public DiscussionWebConfig(DiscussionService discussionService, AuthService authService, PostService postService) {
        this.discussionService = discussionService;
        this.authService = authService;
        this.postService = postService;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        DiscussionEndpoints discussionEndpoints = new DiscussionEndpoints(
                this.discussionService,
                this.authService,
                this.postService
        );

        plugin.registerEndpoints(discussionEndpoints);
    }

}
