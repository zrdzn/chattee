package io.github.zrdzn.web.chattee.backend.storage.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.web.chattee.backend.storage.Poolable;
import io.github.zrdzn.web.chattee.backend.storage.Storage;
import io.github.zrdzn.web.chattee.backend.storage.StorageConfig;
import io.github.zrdzn.web.chattee.backend.storage.StorageType;
import org.tinylog.Logger;

public class PostgresStorage implements Storage, Poolable {

    private HikariDataSource hikariDataSource;
    private String jdbc;

    @Override
    public PostgresStorage load(StorageConfig config) {
        if (!config.getSsl()) {
            Logger.warn("Storage connection is configured without SSL enabled.");
        }

        this.jdbc = String.format("jdbc:postgresql://%s:%s/%s?ssl=%s", config.getHost(), config.getPort(), config.getDb(), config.getSsl());

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(this.jdbc);
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPass());
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
        hikariConfig.setConnectionTimeout(config.getConnectionTimeoutMs());
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        this.hikariDataSource = new HikariDataSource(hikariConfig);

        return this;
    }

    @Override
    public StorageType getType() {
        return StorageType.POSTGRES;
    }

    @Override
    public void stop() {
        this.hikariDataSource.close();
    }

    @Override
    public HikariDataSource getHikariDataSource() {
        return this.hikariDataSource;
    }

    public String getJdbc() {
        return this.jdbc;
    }

}
