package io.github.zrdzn.web.chattee.backend.plan;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

public class DiscussionService {

    private final DiscussionRepository discussionRepository;

    public DiscussionService(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    public Result<Discussion, HttpResponse> createDiscussion(Discussion discussion) {
        return this.discussionRepository.saveDiscussion(discussion)
                .onError(error -> Logger.error(error, "Could not save the discussion."))
                .mapErr(error -> internalServerError("Could not create the discussion."));
    }

    public Result<List<Discussion>, HttpResponse> getAllDiscussions() {
        return this.discussionRepository.listAllDiscussions()
                .onError(error -> Logger.error(error, "Could not list all discussions."))
                .mapErr(error -> internalServerError("Could not retrieve all discussions."));
    }

    public Result<Optional<Discussion>, HttpResponse> getDiscussion(long id) {
        return this.discussionRepository.findDiscussionById(id)
                .onError(error -> Logger.error(error, "Could not find a discussion."))
                .mapErr(error -> internalServerError("Could not retrieve a discussion."));
    }

    public Result<Blank, HttpResponse> removeDiscussion(long id) {
        return this.discussionRepository.deleteDiscussion(id)
                .onError(error -> Logger.error(error, "Could not delete a discussion."))
                .mapErr(error -> internalServerError("Could not remove a discussion."));
    }

}
