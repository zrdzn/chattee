package io.github.zrdzn.web.chattee.backend.discussion;

import java.util.List;
import java.util.stream.Collectors;
import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final AccountService accountService;

    public DiscussionService(DiscussionRepository discussionRepository, AccountService accountService) {
        this.discussionRepository = discussionRepository;
        this.accountService = accountService;
    }

    public Result<DiscussionDetails, HttpResponse> createDiscussion(DiscussionCreateRequest discussionCreateRequest, long authorId) {
        return this.discussionRepository.saveDiscussion(discussionCreateRequest, authorId)
                .map(discussion ->
                        new DiscussionDetails(
                                discussion.getId(),
                                discussion.getCreatedAt(),
                                discussion.getTitle(),
                                discussion.getDescription(),
                                this.accountService.getAccount(authorId).get()
                        )
                )
                .mapErr(error -> {
                    if (error == DiscussionError.ALREADY_EXISTS) {
                        return conflict(DiscussionError.ALREADY_EXISTS.getMessage());
                    } else if (error == DiscussionError.INVALID_ACCOUNT_ID) {
                        return badRequest(DiscussionError.INVALID_ACCOUNT_ID.getMessage());
                    }

                    return internalServerError("Could not create discussion.");
                });
    }

    public Result<List<DiscussionDetails>, HttpResponse> getAllDiscussions() {
        return this.discussionRepository.listAllDiscussions()
                .map(discussions -> discussions.stream()
                        .map(discussion ->
                                new DiscussionDetails(
                                        discussion.getId(),
                                        discussion.getCreatedAt(),
                                        discussion.getTitle(),
                                        discussion.getDescription(),
                                        this.accountService.getAccount(discussion.getAuthorId()).get()
                                )
                        )
                        .collect(Collectors.toList())
                )
                .mapErr(error -> internalServerError("Could not retrieve all discussions."));
    }

    public Result<DiscussionDetails, HttpResponse> getDiscussion(long id) {
        return this.discussionRepository.findDiscussionById(id)
                .map(discussion ->
                        new DiscussionDetails(
                                discussion.getId(),
                                discussion.getCreatedAt(),
                                discussion.getTitle(),
                                discussion.getDescription(),
                                this.accountService.getAccount(discussion.getAuthorId()).get()
                        )
                )
                .mapErr(error -> {
                    if (error == DiscussionError.NOT_EXISTS) {
                        return notFound(DiscussionError.NOT_EXISTS.getMessage());
                    }

                    return internalServerError("Could not retrieve discussion.");
                });
    }

    public Result<Blank, HttpResponse> removeDiscussion(long id) {
        return this.discussionRepository.deleteDiscussion(id)
                .mapErr(error -> internalServerError("Could not remove discussion."));
    }

}
