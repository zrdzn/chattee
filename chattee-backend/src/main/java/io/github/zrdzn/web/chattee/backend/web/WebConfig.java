package io.github.zrdzn.web.chattee.backend.web;

import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public interface WebConfig {

    void initialize(AnnotationsRoutingPlugin plugin);

}
