package io.github.zrdzn.web.chattee.backend.discussion;

import java.util.List;
import panda.std.Blank;
import panda.std.Result;

public interface DiscussionRepository {

    Result<Discussion, DiscussionError> saveDiscussion(DiscussionCreateRequest discussionCreateRequest, long authorId);

    Result<List<Discussion>, DiscussionError> listAllDiscussions();

    Result<Discussion, DiscussionError> findDiscussionById(long id);

    Result<Blank, DiscussionError> deleteDiscussion(long id);

}
