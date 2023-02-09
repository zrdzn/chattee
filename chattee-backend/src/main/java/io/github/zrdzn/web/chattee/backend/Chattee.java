package io.github.zrdzn.web.chattee.backend;

import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.HttpServer;

public class Chattee {

    private final ChatteeConfig chatteeConfig;
    private final HttpServer httpServer;
    private final PostgresStorage postgresStorage;

    public Chattee(ChatteeConfig chatteeConfig, HttpServer httpServer, PostgresStorage postgresStorage) {
        this.chatteeConfig = chatteeConfig;
        this.httpServer = httpServer;
        this.postgresStorage = postgresStorage;
    }

    public void launch() {
        this.httpServer.run(this.chatteeConfig, this.postgresStorage);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        this.httpServer.stop();
        this.postgresStorage.stop();
    }

    public HttpServer getHttpServer() {
        return this.httpServer;
    }

}
