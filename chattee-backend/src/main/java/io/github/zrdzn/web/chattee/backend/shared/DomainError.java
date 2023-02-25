package io.github.zrdzn.web.chattee.backend.shared;

public enum DomainError {

    SQL_EXCEPTION,

    DISCUSSION_ALREADY_EXISTS,
    DISCUSSION_NOT_EXISTS,

    SESSION_ALREADY_EXISTS,
    SESSION_NOT_EXISTS,

    ACCOUNT_ALREADY_EXISTS,
    ACCOUNT_NOT_EXISTS,
    ACCOUNT_INVALID_ID,

    ACCOUNT_PRIVILEGE_NOT_EXISTS,

}