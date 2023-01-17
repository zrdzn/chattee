package io.github.zrdzn.web.chattee.backend.discussion;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

public class DiscussionWriteDto {

    @PositiveOrZero
    private Long authorId;

    @NotBlank
    @Length(min = 3, max = 100)
    private String title;

    @NotBlank
    @Length(min = 1, max = 2000)
    private String description;

    public DiscussionWriteDto() {
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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
