package io.github.zrdzn.web.chattee.backend.plan;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

public class DiscussionFacade {

    private final DiscussionService discussionService;

    public DiscussionFacade(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    public Result<Discussion, HttpResponse> createDiscussion(Discussion discussion) {
        return this.discussionService.createDiscussion(discussion);
    }

    public Result<List<Discussion>, HttpResponse> getAllDiscussions() {
        return this.discussionService.getAllDiscussions();
    }

    public Result<Optional<Discussion>, HttpResponse> getDiscussion(long id) {
        return this.discussionService.getDiscussion(id);
    }

    public Result<Blank, HttpResponse> removeDiscussion(long id) {
        return this.discussionService.removeDiscussion(id);
    }

}
