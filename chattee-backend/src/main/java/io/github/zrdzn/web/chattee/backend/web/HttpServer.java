package io.github.zrdzn.web.chattee.backend.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.zrdzn.web.chattee.backend.ChatteeConfig;
import io.github.zrdzn.web.chattee.backend.plan.DiscussionWebConfig;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.user.UserWebConfig;
import io.github.zrdzn.web.chattee.backend.user.auth.AuthWebConfig;
import io.github.zrdzn.web.chattee.backend.user.session.SessionFacade;
import io.github.zrdzn.web.chattee.backend.user.session.SessionRepository;
import io.github.zrdzn.web.chattee.backend.user.session.SessionService;
import io.github.zrdzn.web.chattee.backend.user.session.infrastructure.PostgresSessionRepository;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenFacade;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import io.javalin.openapi.OpenApiInfo;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class HttpServer {

    private Javalin javalin;

    public void run(ChatteeConfig chatteeConfig, PostgresStorage postgresStorage) {
        this.javalin = Javalin
                .create(config -> {
                    config.http.defaultContentType = ContentType.JSON;

                    config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));

                    config.routing.ignoreTrailingSlashes = true;

                    config.showJavalinBanner = false;

                    this.configureRoutes(config, postgresStorage);
                    this.configureJsonMapper(config);
                    this.configureOpenApi(config);
                    this.configureSwagger(config);
                }).events(event -> event.serverStopping(postgresStorage::stop))
                .error(HttpStatus.NOT_FOUND.getCode(), context -> context.status(HttpStatus.NOT_FOUND).json(HttpResponse.notFound("Page not found.")))
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), context -> context.status(HttpStatus.INTERNAL_SERVER_ERROR).json(HttpResponse.internalServerError("Something went wrong.")))
                .exception(JsonParseException.class, (exception, context) ->
                        context.status(HttpStatus.BAD_REQUEST).json(HttpResponse.badRequest("You have provided invalid details.")))
                .start(chatteeConfig.getPort());
    }

    public void stop() {
        this.javalin.stop();
    }

    public Javalin getJavalin() {
        return this.javalin;
    }

    private void configureRoutes(JavalinConfig config, PostgresStorage postgresStorage) {
        AnnotationsRoutingPlugin plugin = new AnnotationsRoutingPlugin();

        UserWebConfig userWebConfig = new UserWebConfig(postgresStorage);
        userWebConfig.initialize(plugin);

        new DiscussionWebConfig(postgresStorage).initialize(plugin);

        SessionRepository sessionRepository = new PostgresSessionRepository(postgresStorage);
        SessionService sessionService = new SessionService(sessionRepository, new AccessTokenFacade(new AccessTokenService()));
        SessionFacade sessionFacade = new SessionFacade(sessionService);

        new AuthWebConfig(userWebConfig.getUserFacade(), sessionFacade).initialize(plugin);

        config.plugins.register(plugin);
    }

    private void configureJsonMapper(JavalinConfig config) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new SimpleModule())
                .build()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        config.jsonMapper(new JavalinJackson(objectMapper));
    }

    private void configureOpenApi(JavalinConfig config) {
        OpenApiConfiguration openApiConfig = new OpenApiConfiguration();

        OpenApiInfo openApiInfo = new OpenApiInfo();
        openApiInfo.setVersion("1.0.0");
        openApiInfo.setTitle("Chattee API");
        openApiInfo.setDescription("Official API of the Chattee.");

        openApiConfig.setInfo(openApiInfo);

        config.plugins.register(new OpenApiPlugin(openApiConfig));
    }

    private void configureSwagger(JavalinConfig config) {
        SwaggerConfiguration swaggerConfig = new SwaggerConfiguration();
        swaggerConfig.setTitle("Chattee API");
        config.plugins.register(new SwaggerPlugin(swaggerConfig));
    }

}