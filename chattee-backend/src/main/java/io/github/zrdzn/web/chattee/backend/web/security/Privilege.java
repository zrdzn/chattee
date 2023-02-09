package io.github.zrdzn.web.chattee.backend.web.security;

import io.javalin.security.RouteRole;

public enum Privilege implements RouteRole {

    GUEST,

    DISCUSSION_OPEN

}
