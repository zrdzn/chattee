package io.github.zrdzn.web.chattee.backend.discussion.post;

import java.util.List;
import java.util.stream.Collectors;
import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.discussion.DiscussionService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class PostService {

    private final PostRepository postRepository;
    private final AccountService accountService;
    private final DiscussionService discussionService;

    public PostService(PostRepository postRepository, AccountService accountService, DiscussionService discussionService) {
        this.postRepository = postRepository;
        this.accountService = accountService;
        this.discussionService = discussionService;
    }

    public Result<PostDetails, HttpResponse> createPost(PostCreateRequest postCreateRequest, long authorId, long discussionId) {
        return this.postRepository.savePost(postCreateRequest, authorId, discussionId)
                .map(post ->
                        new PostDetails(
                                post.getId(),
                                post.getCreatedAt(),
                                post.getContent(),
                                this.accountService.getAccount(authorId).get(),
                                this.discussionService.getDiscussion(discussionId).get()
                        )
                )
                .mapErr(error -> {
                    if (error == PostError.ID_TARGETS_INVALID_RECORD) {
                        return badRequest(PostError.ID_TARGETS_INVALID_RECORD.getMessage());
                    }

                    return internalServerError("Could not create post.");
                });
    }

    public Result<List<PostDetails>, HttpResponse> findPostsByDiscussionId(long discussionId) {
        return this.postRepository.findPostsByDiscussionId(discussionId)
                .map(posts -> posts.stream()
                        .map(post ->
                                new PostDetails(
                                        post.getId(),
                                        post.getCreatedAt(),
                                        post.getContent(),
                                        this.accountService.getAccount(post.getAuthorId()).get(),
                                        this.discussionService.getDiscussion(discussionId).get()
                                )
                        )
                        .collect(Collectors.toList())
                )
                .mapErr(error -> internalServerError("Could not retrieve all posts."));
    }

    public Result<PostDetails, HttpResponse> getPost(long id) {
        return this.postRepository.findPostById(id)
                .map(post ->
                        new PostDetails(
                                post.getId(),
                                post.getCreatedAt(),
                                post.getContent(),
                                this.accountService.getAccount(post.getAuthorId()).get(),
                                this.discussionService.getDiscussion(post.getDiscussionId()).get()
                        )
                )
                .mapErr(error -> {
                    if (error == PostError.NOT_EXISTS) {
                        return notFound(PostError.NOT_EXISTS.getMessage());
                    }

                    return internalServerError("Could not retrieve post.");
                });
    }

    public Result<Blank, HttpResponse> removePost(long id) {
        return this.postRepository.deletePostById(id)
                .mapErr(error -> internalServerError("Could not remove post."));
    }

}
