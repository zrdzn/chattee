package io.github.zrdzn.web.chattee.backend.discussion;

public class DiscussionCreateDto {

    private String title;
    private String description;

    public DiscussionCreateDto() {
    }

    public DiscussionCreateDto(String title, String description) {
        this.title = title;
        this.description = description;
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

}
