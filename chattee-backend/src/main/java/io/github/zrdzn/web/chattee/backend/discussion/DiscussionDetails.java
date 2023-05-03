package io.github.zrdzn.web.chattee.backend.discussion;

import java.time.Instant;
import io.github.zrdzn.web.chattee.backend.account.AccountDetails;
import io.github.zrdzn.web.chattee.backend.discussion.post.PostDetails;

public class DiscussionDetails {

    private long id;
    private Instant createdAt;
    private String title;
    private String description;
    private AccountDetails author;
    private int postsAmount;
    private PostDetails latestPost;

    public DiscussionDetails() {
    }

    public DiscussionDetails(long id, Instant createdAt, String title, String description, AccountDetails author, int postsAmount, PostDetails latestPost) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.author = author;
        this.postsAmount = postsAmount;
        this.latestPost = latestPost;
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

    public AccountDetails getAuthor() {
        return this.author;
    }

    public void setAuthor(AccountDetails author) {
        this.author = author;
    }

    public int getPostsAmount() {
        return this.postsAmount;
    }

    public void setPostsAmount(int postsAmount) {
        this.postsAmount = postsAmount;
    }

    public PostDetails getLatestPost() {
        return this.latestPost;
    }

    public void setLatestPost(PostDetails latestPost) {
        this.latestPost = latestPost;
    }

}
