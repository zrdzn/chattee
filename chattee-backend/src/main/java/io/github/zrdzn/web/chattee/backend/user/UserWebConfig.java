package io.github.zrdzn.web.chattee.backend.user;

import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.user.infrastructure.PostgresUserRepository;
import io.github.zrdzn.web.chattee.backend.web.WebConfig;
import io.javalin.community.routing.annotations.AnnotationsRoutingPlugin;

public class UserWebConfig implements WebConfig {

    private final PostgresStorage postgresStorage;

    private UserFacade userFacade;

    public UserWebConfig(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public void initialize(AnnotationsRoutingPlugin plugin) {
        UserRepository userRepository = new PostgresUserRepository(this.postgresStorage);
        UserService userService = new UserService(userRepository);
        this.userFacade = new UserFacade(userService);
        UserEndpoints userEndpoints = new UserEndpoints(this.userFacade);
        plugin.registerEndpoints(userEndpoints);
    }

    public UserFacade getUserFacade() {
        return this.userFacade;
    }

}
