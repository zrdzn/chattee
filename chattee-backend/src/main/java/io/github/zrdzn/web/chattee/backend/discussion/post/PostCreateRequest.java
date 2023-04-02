package io.github.zrdzn.web.chattee.backend.discussion.post;

public class PostCreateRequest {

    private String content;
    private long discussionId;

    public PostCreateRequest() {
    }

    public PostCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDiscussionId() {
        return this.discussionId;
    }

    public void setDiscussionId(long discussionId) {
        this.discussionId = discussionId;
    }

}
