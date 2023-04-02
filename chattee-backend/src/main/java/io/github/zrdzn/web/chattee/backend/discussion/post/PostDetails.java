package io.github.zrdzn.web.chattee.backend.discussion.post;

import java.time.Instant;
import io.github.zrdzn.web.chattee.backend.account.AccountDetails;
import io.github.zrdzn.web.chattee.backend.discussion.DiscussionDetails;

public class PostDetails {

    private long id;
    private Instant createdAt;
    private String content;
    private AccountDetails author;
    private DiscussionDetails discussion;

    public PostDetails() {
    }

    public PostDetails(long id, Instant createdAt, String content, AccountDetails author, DiscussionDetails discussion) {
        this.id = id;
        this.createdAt = createdAt;
        this.content = content;
        this.author = author;
        this.discussion = discussion;
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

    public AccountDetails getAuthor() {
        return this.author;
    }

    public void setAuthor(AccountDetails author) {
        this.author = author;
    }

    public DiscussionDetails getDiscussion() {
        return this.discussion;
    }

    public void setDiscussion(DiscussionDetails discussion) {
        this.discussion = discussion;
    }

}
