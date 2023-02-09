package io.github.zrdzn.web.chattee.backend.plan;

import java.util.List;
import java.util.Optional;
import panda.std.Blank;
import panda.std.Result;

public interface DiscussionRepository {

    Result<Discussion, Exception> saveDiscussion(Discussion discussion);

    Result<List<Discussion>, Exception> listAllDiscussions();

    Result<Optional<Discussion>, Exception> findDiscussionById(long id);

    Result<Blank, Exception> deleteDiscussion(long id);

}
