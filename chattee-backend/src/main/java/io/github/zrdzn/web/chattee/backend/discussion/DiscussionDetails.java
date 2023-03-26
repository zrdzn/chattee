package io.github.zrdzn.web.chattee.backend.discussion;

import java.time.Instant;
import io.github.zrdzn.web.chattee.backend.account.Account;

public class DiscussionDetails {

    private long id;
    private Instant createdAt;
    private String title;
    private String description;
    private Account author;

    public DiscussionDetails() {
    }

    public DiscussionDetails(long id, Instant createdAt, String title, String description, Account author) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.author = author;
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

    public Account getAuthor() {
        return this.author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

}
