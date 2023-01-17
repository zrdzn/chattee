package io.github.zrdzn.web.chattee.backend.discussion;

import java.util.List;
import java.util.Optional;

public interface DiscussionService {

    Discussion createDiscussion(Discussion discussion);

    Optional<Discussion> getDiscussion(long id);

    List<Discussion> getAllDiscussions();

    void removeDiscussion(long id);

}
