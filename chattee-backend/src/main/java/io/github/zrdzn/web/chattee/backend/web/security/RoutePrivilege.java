package io.github.zrdzn.web.chattee.backend.web.security;

import java.util.List;
import io.javalin.security.RouteRole;

public enum RoutePrivilege implements RouteRole {

    DISCUSSION_OPEN,
    DISCUSSION_VIEW_ALL,
    DISCUSSION_VIEW,
    DISCUSSION_DELETE,

    POST_CREATE,
    POST_VIEW,
    POST_REMOVE,

    AUTH_DETAILS_VIEW,

    ACCOUNT_VIEW_ALL,
    ACCOUNT_VIEW,
    ACCOUNT_DELETE,

    ACCOUNT_PRIVILEGE_VIEW_ALL,

    ADMINISTRATOR;

    public static final List<RoutePrivilege> DEFAULT_ROLES = List.of(
            DISCUSSION_OPEN, DISCUSSION_VIEW_ALL, DISCUSSION_VIEW, DISCUSSION_DELETE,
            POST_CREATE, POST_VIEW, POST_REMOVE,
            AUTH_DETAILS_VIEW,
            ACCOUNT_VIEW, ACCOUNT_DELETE
    );

}
