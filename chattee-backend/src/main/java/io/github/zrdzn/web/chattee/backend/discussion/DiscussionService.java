package io.github.zrdzn.web.chattee.backend.discussion;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class DiscussionService {

    private final DiscussionRepository discussionRepository;

    public DiscussionService(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    public Result<Discussion, HttpResponse> createDiscussion(Discussion discussion) {
        return this.discussionRepository.saveDiscussion(discussion)
                .mapErr(error -> {
                    if (error == DomainError.DISCUSSION_ALREADY_EXISTS) {
                        return conflict("Discussion already exists.");
                    } else if (error == DomainError.ACCOUNT_INVALID_ID) {
                        return badRequest("'authorId' does not target existing record.");
                    }

                    return internalServerError("Could not create discussion.");
                });
    }

    public Result<List<Discussion>, HttpResponse> getAllDiscussions() {
        return this.discussionRepository.listAllDiscussions()
                .mapErr(error -> internalServerError("Could not retrieve all discussions."));
    }

    public Result<Discussion, HttpResponse> getDiscussion(long id) {
        return this.discussionRepository.findDiscussionById(id)
                .mapErr(error -> {
                    if (error == DomainError.DISCUSSION_NOT_EXISTS) {
                        return notFound("Discussion does not exist.");
                    }

                    return internalServerError("Could not retrieve discussion.");
                });
    }

    public Result<Blank, HttpResponse> removeDiscussion(long id) {
        return this.discussionRepository.deleteDiscussion(id)
                .mapErr(error -> internalServerError("Could not remove discussion."));
    }

}
