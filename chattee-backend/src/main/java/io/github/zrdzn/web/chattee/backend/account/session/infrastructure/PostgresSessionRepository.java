package io.github.zrdzn.web.chattee.backend.account.session.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.session.Session;
import io.github.zrdzn.web.chattee.backend.account.session.SessionRepository;
import panda.std.Blank;
import panda.std.Result;

public class PostgresSessionRepository implements SessionRepository {

    private static final String INSERT_SESSION = "insert into sessions (token, account_id, expire_at, ip_address) values (?, ?, ?, ?);";

    private static final String SELECT_ALL_SESSIONS = "select token, account_id, created_at, expire_at, ip_address from sessions;";

    private static final String SELECT_SESSION_BY_TOKEN = "select account_id, created_at, expire_at, ip_address from sessions where token = ?;";

    private static final String DELETE_SESSION_BY_TOKEN = "delete from sessions where token = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresSessionRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<Session, Exception> saveSession(Session session) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT_SESSION)) {
                statement.setString(1, session.getToken());
                statement.setLong(2, session.getAccountId());
                statement.setTimestamp(3, Timestamp.from(session.getExpireAt()));
                statement.setString(4, session.getIpAddress());

                statement.executeUpdate();

                return session;
            }
        });
    }

    @Override
    public Result<List<Session>, Exception> listAllSessions() {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SESSIONS)) {
                List<Session> sessions = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    String token = result.getString("token");
                    long userId = result.getLong("user_id");
                    Instant expireAt = result.getTimestamp("expire_at").toInstant();
                    Instant createdAt = result.getTimestamp("created_at").toInstant();
                    String ipAddress = result.getString("ip_address");

                    sessions.add(new Session(token, userId, expireAt, createdAt, ipAddress));
                }

                return sessions;
            }
        });
    }

    @Override
    public Result<Optional<Session>, Exception> findSessionByToken(String token) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_SESSION_BY_TOKEN)) {
                statement.setString(1, token);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                long userId = result.getLong("user_id");
                Instant expireAt = result.getTimestamp("expire_at").toInstant();
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                String ipAddress = result.getString("ip_address");

                return Optional.of(new Session(token, userId, expireAt, createdAt, ipAddress));
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteSessionByToken(String token) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(DELETE_SESSION_BY_TOKEN)) {
                statement.setString(1, token);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

}
