package io.github.zrdzn.web.chattee.backend.account.auth.details;

public enum AuthDetailsError {

    ALREADY_EXIST("Auth details already exist."),
    NOT_EXIST("Auth details do not exist."),
    INVALID_ACCOUNT_ID("Author id does not target existing record."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    AuthDetailsError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
