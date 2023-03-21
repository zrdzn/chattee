package io.github.zrdzn.web.chattee.backend.account.auth;

import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.JDBCSessionDataStoreFactory;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;

public class AuthSessionHandlerFactory {

    public static SessionHandler createPostgres(PostgresStorage storage) {
        SessionHandler sessionHandler = new SessionHandler();

        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);

        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDriverInfo("org.postgresql.Driver", storage.getJdbc());
        databaseAdaptor.setDatasource(storage.getHikariDataSource());
        JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
        jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
        sessionCache.setSessionDataStore(jdbcSessionDataStoreFactory.getSessionDataStore(sessionHandler));

        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSessionCookie("SESSIONID");

        return sessionHandler;
    }

}
