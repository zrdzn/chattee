package io.github.zrdzn.web.chattee.backend.discussion;

import java.time.Instant;

public class Discussion {

    private long id;
    private Instant createdAt;
    private String title;
    private String description;
    private long authorId;

    public Discussion() {
    }

    public Discussion(long id, Instant createdAt, String title, String description, long authorId) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

}
