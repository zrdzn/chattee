package io.github.zrdzn.web.chattee.backend.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.zrdzn.web.chattee.backend.ChatteeConfig;
import io.github.zrdzn.web.chattee.backend.account.AccountService;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsRepository;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsService;
import io.github.zrdzn.web.chattee.backend.account.auth.details.infrastructure.PostgresAuthDetailsRepository;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeRepository;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeService;
import io.github.zrdzn.web.chattee.backend.account.privilege.PrivilegeWebConfig;
import io.github.zrdzn.web.chattee.backend.account.privilege.repositories.PostgresPrivilegeRepository;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthSessionHandlerFactory;
import io.github.zrdzn.web.chattee.backend.discussion.DiscussionWebConfig;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.AccountWebConfig;
import io.github.zrdzn.web.chattee.backend.account.auth.AuthWebConfig;
import io.github.zrdzn.web.chattee.backend.web.security.token.AccessTokenService;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

public class HttpServer {

    private Javalin javalin;

    public void run(ChatteeConfig chatteeConfig, PostgresStorage postgresStorage) {
        this.javalin = Javalin
                .create(config -> {
                    config.http.defaultContentType = ContentType.JSON;

                    config.plugins.enableCors(cors -> cors.add(corsConfig -> {
                        corsConfig.allowHost("http://localhost:3000");
                        corsConfig.allowCredentials = true;
                    }));

                    config.routing.ignoreTrailingSlashes = true;

                    config.showJavalinBanner = false;

                    this.configureRoutes(config, postgresStorage);
                    this.configureJsonMapper(config);
                    this.configureOpenApi(config);
                    this.configureSwagger(config);

                    config.jetty.sessionHandler(() -> AuthSessionHandlerFactory.createPostgres(postgresStorage));
                }).events(event -> event.serverStopping(postgresStorage::stop))
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

        PrivilegeRepository privilegeRepository = new PostgresPrivilegeRepository(postgresStorage);
        PrivilegeService privilegeService = new PrivilegeService(privilegeRepository);

        AuthDetailsRepository authDetailsRepository = new PostgresAuthDetailsRepository(postgresStorage);
        AuthDetailsService authDetailsService = new AuthDetailsService(authDetailsRepository, new AccessTokenService());

        AuthService authService = new AuthService(authDetailsService, privilegeService);

        AccountWebConfig accountWebConfig = new AccountWebConfig(postgresStorage, authService);
        accountWebConfig.initialize(plugin);
        AccountService accountService = accountWebConfig.getAccountService();

        new PrivilegeWebConfig(privilegeService, accountService, authService).initialize(plugin);

        new DiscussionWebConfig(postgresStorage, authService).initialize(plugin);

        new AuthWebConfig(accountService, authService).initialize(plugin);

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
        config.plugins.register(new OpenApiPlugin(
                new OpenApiPluginConfiguration()
                        .withDefinitionConfiguration((version, definition) -> definition
                                .withOpenApiInfo((openApiInfo) -> {
                                    openApiInfo.setTitle("Chattee API");
                                    openApiInfo.setVersion("1.0.0");
                                    openApiInfo.setDescription("Official API of the Chattee");
                                })
                )
        ));
    }

    private void configureSwagger(JavalinConfig config) {
        SwaggerConfiguration swaggerConfig = new SwaggerConfiguration();
        swaggerConfig.setTitle("Chattee API");
        config.plugins.register(new SwaggerPlugin(swaggerConfig));
    }

}
