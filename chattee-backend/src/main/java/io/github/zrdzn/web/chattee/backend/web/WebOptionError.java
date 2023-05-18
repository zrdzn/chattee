package io.github.zrdzn.web.chattee.backend.web;

public enum WebOptionError {

    ALREADY_EXISTS("Web option already exists."),
    NOT_EXISTS("Web option does not exist."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    WebOptionError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
