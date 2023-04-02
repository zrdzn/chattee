package io.github.zrdzn.web.chattee.backend.discussion.post;

import java.time.Instant;

public class Post {

    private long id;
    private Instant createdAt;
    private String content;
    private long authorId;
    private long discussionId;

    public Post() {
    }

    public Post(long id, Instant createdAt, String content, long authorId, long discussionId) {
        this.id = id;
        this.createdAt = createdAt;
        this.content = content;
        this.authorId = authorId;
        this.discussionId = discussionId;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getDiscussionId() {
        return this.discussionId;
    }

    public void setDiscussionId(long discussionId) {
        this.discussionId = discussionId;
    }

}
