package io.github.zrdzn.web.chattee.backend.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionRepository repository;

    @Autowired
    public DiscussionServiceImpl(DiscussionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Discussion createDiscussion(Discussion discussion) {
        return this.repository.save(discussion);
    }

    @Override
    public Optional<Discussion> getDiscussion(long id) {
        return this.repository.findById(id);
    }

    @Override
    public List<Discussion> getAllDiscussions() {
        return this.repository.findAll();
    }

    @Override
    public void removeDiscussion(long id) {
        this.repository.deleteById(id);
    }

}
