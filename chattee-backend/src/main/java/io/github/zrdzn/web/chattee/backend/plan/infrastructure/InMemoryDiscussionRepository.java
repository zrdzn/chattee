package io.github.zrdzn.web.chattee.backend.plan.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.plan.Discussion;
import io.github.zrdzn.web.chattee.backend.plan.DiscussionRepository;
import panda.std.Blank;
import panda.std.Result;

public class InMemoryDiscussionRepository implements DiscussionRepository {

    private final Map<Long, Discussion> discussions = new HashMap<>();

    @Override
    public Result<Discussion, Exception> saveDiscussion(Discussion discussion) {
        this.discussions.put(discussion.getId(), discussion);
        return Result.ok(discussion);
    }

    @Override
    public Result<List<Discussion>, Exception> listAllDiscussions() {
        return Result.ok(new ArrayList<>(this.discussions.values()));
    }

    @Override
    public Result<Optional<Discussion>, Exception> findDiscussionById(long id) {
        return Result.ok(Optional.ofNullable(this.discussions.get(id)));
    }

    @Override
    public Result<Blank, Exception> deleteDiscussion(long id) {
        this.discussions.remove(id);
        return Result.ok();
    }

}
