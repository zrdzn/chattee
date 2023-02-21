package io.github.zrdzn.web.chattee.backend.account.session.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.session.Session;
import io.github.zrdzn.web.chattee.backend.account.session.SessionRepository;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
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
    public Result<Session, DomainError> saveSession(Session session) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SESSION)) {
            statement.setString(1, session.getToken());
            statement.setLong(2, session.getAccountId());
            statement.setTimestamp(3, Timestamp.from(session.getExpireAt()));
            statement.setString(4, session.getIpAddress());

            statement.executeUpdate();

            return Result.ok(session);
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(DomainError.SESSION_ALREADY_EXISTS);
            } else if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_INVALID_ID);
            }

            Logger.error(exception, "Could not save session");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Session>, DomainError> listAllSessions() {
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

            return Result.ok(sessions);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not list all sessions.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Session, DomainError> findSessionByToken(String token) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SESSION_BY_TOKEN)) {
            statement.setString(1, token);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DomainError.SESSION_NOT_EXISTS);
            }

            long userId = result.getLong("user_id");
            Instant expireAt = result.getTimestamp("expire_at").toInstant();
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            String ipAddress = result.getString("ip_address");

            return Result.ok(new Session(token, userId, expireAt, createdAt, ipAddress));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find session.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DomainError> deleteSessionByToken(String token) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SESSION_BY_TOKEN)) {
            statement.setString(1, token);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete session.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

}
