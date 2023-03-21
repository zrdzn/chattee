package io.github.zrdzn.web.chattee.backend.web.security;

import io.javalin.security.RouteRole;

public enum RoutePrivilege implements RouteRole {

    DISCUSSION_OPEN,
    DISCUSSION_VIEW_ALL,
    DISCUSSION_VIEW,
    DISCUSSION_DELETE,

    AUTH_DETAILS_VIEW,

    ACCOUNT_VIEW_ALL,
    ACCOUNT_VIEW,
    ACCOUNT_DELETE,

    ACCOUNT_PRIVILEGE_VIEW_ALL,

    ADMINISTRATOR

}
