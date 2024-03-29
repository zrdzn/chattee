package io.github.zrdzn.web.chattee.backend.account.privilege;

public enum PrivilegeError {

    ALREADY_EXISTS("Privilege already exists."),
    SQL_EXCEPTION("Something went wrong with database.");

    private final String message;

    PrivilegeError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
