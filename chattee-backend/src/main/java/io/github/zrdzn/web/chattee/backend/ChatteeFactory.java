package io.github.zrdzn.web.chattee.backend;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.HttpServer;

public class ChatteeFactory {

    public static Chattee createDefault() {
        ChatteeConfig config = ConfigManager.create(ChatteeConfig.class, (okaeriConfig) -> {
            okaeriConfig.withConfigurer(new YamlSnakeYamlConfigurer());
            okaeriConfig.withBindFile("app.yml");
            okaeriConfig.saveDefaults();
            okaeriConfig.load(true);
        });

        PostgresStorage postgresStorage = new PostgresStorage().load(config.getStorage());

        return new Chattee(config, new HttpServer(), postgresStorage);
    }

}
