package io.github.zrdzn.web.chattee.backend.account.auth.details.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetails;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsCreateRequest;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.auth.details.AuthDetailsRepository;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

public class PostgresAuthDetailsRepository implements AuthDetailsRepository {

    private static final String INSERT_AUTH_DETAILS = "insert into auth_details (token, created_at, account_id, expire_at, ip_address) values (?, ?, ?, ?, ?);";

    private static final String SELECT_ALL_AUTH_DETAILS_BY_ACCOUNT_ID = "select token, created_at, expire_at, ip_address from auth_details where account_id = ?;";

    private static final String SELECT_AUTH_DETAILS_BY_TOKEN = "select account_id, created_at, expire_at, ip_address from auth_details where token = ?;";

    private static final String DELETE_AUTH_DETAILS_BY_TOKEN = "delete from auth_details where token = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresAuthDetailsRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<AuthDetails, DomainError> saveAuthDetails(AuthDetailsCreateRequest authDetailsCreateRequest, String token) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_AUTH_DETAILS)) {
            Instant createdAt = Instant.now();

            statement.setString(1, token);
            statement.setTimestamp(2, Timestamp.from(createdAt));
            statement.setLong(3, authDetailsCreateRequest.getAccountId());
            statement.setTimestamp(4, Timestamp.from(authDetailsCreateRequest.getExpireAt()));
            statement.setString(5, authDetailsCreateRequest.getIpAddress());

            statement.executeUpdate();

            return Result.ok(
                    new AuthDetails(
                            token,
                            createdAt,
                            authDetailsCreateRequest.getAccountId(),
                            authDetailsCreateRequest.getExpireAt(),
                            authDetailsCreateRequest.getIpAddress()
                    )
            );
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(DomainError.AUTH_DETAILS_ALREADY_EXIST);
            } else if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_INVALID_ID);
            }

            Logger.error(exception, "Could not save auth details");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<AuthDetails>, DomainError> listAllAuthDetailsByAccountId(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_AUTH_DETAILS_BY_ACCOUNT_ID)) {
            List<AuthDetails> authDetails = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String token = result.getString("token");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                Instant expireAt = result.getTimestamp("expire_at").toInstant();
                String ipAddress = result.getString("ip_address");

                authDetails.add(new AuthDetails(token, createdAt, id, expireAt, ipAddress));
            }

            return Result.ok(authDetails);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not list all auth details.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<AuthDetails, DomainError> findAuthDetailsByToken(String token) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AUTH_DETAILS_BY_TOKEN)) {
            statement.setString(1, token);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DomainError.AUTH_DETAILS_NOT_EXIST);
            }

            long accountId = result.getLong("account_id");
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            Instant expireAt = result.getTimestamp("expire_at").toInstant();
            String ipAddress = result.getString("ip_address");

            return Result.ok(new AuthDetails(token, createdAt, accountId, expireAt, ipAddress));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find auth details.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DomainError> deleteAuthDetailsByToken(String token) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_AUTH_DETAILS_BY_TOKEN)) {
            statement.setString(1, token);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete auth details.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

}
