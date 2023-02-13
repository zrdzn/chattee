package io.github.zrdzn.web.chattee.backend.plan;

public class Discussion {

    private long id;
    private String title;
    private String description;
    private long authorId;

    public Discussion() {
    }

    public Discussion(String title, String description, long authorId) {
        this(0L, title, description, authorId);
    }

    public Discussion(long id, String title, String description, long authorId) {
        this.id = id;
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
