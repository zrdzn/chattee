package io.github.zrdzn.web.chattee.backend.discussion.post;

public enum PostError {

    NOT_EXISTS("Post does not exist."),
    ID_TARGETS_INVALID_RECORD("Either author or discussion id does not target existing record."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    PostError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
