package io.github.zrdzn.web.chattee.backend;

import java.io.File;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.HttpServer;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnFactory;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.tinylog.Logger;
import panda.std.Result;

public class ChatteeFactory {

    public static Chattee createDefault() {
        Cdn cdn = CdnFactory.createStandard();

        File configFile = new File("app.cdn");

        Resource resource = Source.of(configFile);

        Result<ChatteeConfig, CdnException> config = cdn.load(resource, new ChatteeConfig());

        ChatteeConfig chatteeConfig = config.peek(peekConfig -> {
            if (!configFile.exists()) {
                cdn.render(peekConfig, resource);
            }
        }).onError(error -> Logger.error(error, "Could not load the Itemly app configuration file.")).get();

        PostgresStorage postgresStorage = new PostgresStorage().load(cdn);

        return new Chattee(chatteeConfig, new HttpServer(), postgresStorage);
    }

}
