package io.github.zrdzn.web.chattee.backend.storage.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import io.github.zrdzn.web.chattee.backend.storage.Poolable;
import io.github.zrdzn.web.chattee.backend.storage.Storage;
import io.github.zrdzn.web.chattee.backend.storage.StorageConfig;
import io.github.zrdzn.web.chattee.backend.storage.StorageType;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.tinylog.Logger;
import panda.std.Result;

public class PostgresStorage implements Storage, Poolable {

    private HikariDataSource hikariDataSource;
    private String jdbc;

    @Override
    public PostgresStorage load(Cdn cdn) {
        File configFile = new File("storage.cdn");

        Resource resource = Source.of(configFile);

        Result<StorageConfig, CdnException> config = cdn.load(resource, new StorageConfig());

        config.peek(storage -> {
            if (!configFile.exists()) {
                cdn.render(storage, resource);
            }

            if (storage.getSsl().equals("false")) {
                Logger.warn("Storage connection is configured without SSL enabled.");
            }

            this.jdbc = String.format("jdbc:postgresql://%s:%s/%s?ssl=%s", storage.getHost(), storage.getPort(), storage.getDb(), storage.getSsl());

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(this.jdbc);
            hikariConfig.setUsername(storage.getUser());
            hikariConfig.setPassword(storage.getPass());
            hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
            hikariConfig.setLeakDetectionThreshold(60000L);
            hikariConfig.setMaximumPoolSize(storage.getMaxPoolSize());
            hikariConfig.setConnectionTimeout(storage.getConnectionTimeoutMs());
            hikariConfig.setMinimumIdle(0);
            hikariConfig.setIdleTimeout(30000L);

            this.hikariDataSource = new HikariDataSource(hikariConfig);
        }).onError(error -> Logger.error(error, "Could not set properties for the HikariCP data source."));

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
