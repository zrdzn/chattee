package io.github.zrdzn.web.chattee.backend.discussion.post;

import java.util.List;
import panda.std.Blank;
import panda.std.Result;

public interface PostRepository {

    Result<Post, PostError> savePost(PostCreateRequest postCreateRequest, long authorId, long discussionId);

    Result<List<Post>, PostError> findPostsByDiscussionId(long discussionId);

    Result<Post, PostError> findPostById(long id);

    Result<Blank, PostError> deletePostById(long id);

}
