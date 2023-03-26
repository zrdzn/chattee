package io.github.zrdzn.web.chattee.backend.discussion;

import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import panda.std.Blank;
import panda.std.Result;

public interface DiscussionRepository {

    Result<Discussion, DomainError> saveDiscussion(DiscussionCreateRequest discussionCreateRequest, long authorId);

    Result<List<Discussion>, DomainError> listAllDiscussions();

    Result<Discussion, DomainError> findDiscussionById(long id);

    Result<Blank, DomainError> deleteDiscussion(long id);

}
