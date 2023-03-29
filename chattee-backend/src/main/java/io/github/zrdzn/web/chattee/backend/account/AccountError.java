package io.github.zrdzn.web.chattee.backend.account;

public enum AccountError {

    ALREADY_EXISTS("Account already exists."),
    NOT_EXISTS("Account does not exist."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    AccountError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
