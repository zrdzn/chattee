package io.github.zrdzn.web.chattee.backend.discussion;

public enum DiscussionError {

    ALREADY_EXISTS("Discussion already exists."),
    NOT_EXISTS("Discussion does not exist."),
    INVALID_ACCOUNT_ID("Author id does not target existing record."),
    LATEST_POST_NOT_EXISTS("Latest post from this discussion does not exist."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    DiscussionError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
