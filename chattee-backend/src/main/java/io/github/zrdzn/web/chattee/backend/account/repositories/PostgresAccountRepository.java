package io.github.zrdzn.web.chattee.backend.account.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.account.AccountCreateRequest;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.AccountRepository;
import io.github.zrdzn.web.chattee.backend.account.privilege.Privilege;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

public class PostgresAccountRepository implements AccountRepository {

    private static final String INSERT_ACCOUNT = "insert into accounts (created_at, updated_at, email, password, username) values (?, ?, ?, ?, ?);";
    private static final String INSERT_PRIVILEGE = "insert into accounts_privileges (account_id, privilege) values (?, ?);";

    private static final String SELECT_ALL_ACCOUNTS = "select id, created_at, updated_at, email, password, username from accounts;";

    private static final String SELECT_ACCOUNT_BY_ID = "select created_at, updated_at, email, password, username from accounts where id = ?;";
    private static final String SELECT_ACCOUNT_BY_EMAIL = "select id, created_at, updated_at, password, username from accounts where email = ?;";

    private static final String DELETE_ACCOUNT_BY_ID = "delete from accounts where id = ?;";
    private static final String DELETE_PRIVILEGE_BY_ID = "delete from accounts_privileges where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresAccountRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<Account, DomainError> saveAccount(AccountCreateRequest accountCreateRequest) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            Instant createdAt = Instant.now();

            statement.setTimestamp(1, Timestamp.from(createdAt));
            statement.setTimestamp(2, Timestamp.from(createdAt));
            statement.setString(3, accountCreateRequest.getEmail());
            statement.setString(4, accountCreateRequest.getPassword());
            statement.setString(5, accountCreateRequest.getUsername());
            statement.executeUpdate();

            long id = 0L;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }

            return Result.ok(
                    new Account(
                            id,
                            createdAt,
                            createdAt,
                            accountCreateRequest.getEmail(),
                            accountCreateRequest.getPassword(),
                            accountCreateRequest.getUsername()
                    )
            );
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_ALREADY_EXISTS);
            } else if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_INVALID_ID);
            }

            Logger.error(exception, "Could not save account.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Privilege, DomainError> savePrivilege(Privilege privilege) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PRIVILEGE)) {
            statement.setLong(1, privilege.getAccountId());
            statement.setString(2, privilege.getPrivilege().name());

            statement.executeUpdate();

            return Result.ok(privilege);
        } catch (SQLException exception) {
            if (exception.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_INVALID_ID);
            }

            Logger.error(exception, "Could not save privilege.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Account>, DomainError> listAllAccounts() {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ACCOUNTS)) {
            List<Account> accounts = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong("id");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                Instant updatedAt = result.getTimestamp("updated_at").toInstant();
                String email = result.getString("email");
                String password = result.getString("password");
                String username = result.getString("username");

                accounts.add(new Account(id, createdAt, updatedAt, email, password, username));
            }

            return Result.ok(accounts);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not list all accounts.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Account, DomainError> findAccountById(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DomainError.ACCOUNT_NOT_EXISTS);
            }

            Instant createdAt = result.getTimestamp("created_at").toInstant();
            Instant updatedAt = result.getTimestamp("updated_at").toInstant();
            String email = result.getString("email");
            String password = result.getString("password");
            String username = result.getString("username");

            return Result.ok(new Account(id, createdAt, updatedAt, email, password, username));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find account.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Account, DomainError> findAccountByEmail(String email) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT_BY_EMAIL)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DomainError.ACCOUNT_NOT_EXISTS);
            }

            long id = result.getLong("id");
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            Instant updatedAt = result.getTimestamp("updated_at").toInstant();
            String password = result.getString("password");
            String username = result.getString("username");

            return Result.ok(new Account(id, createdAt, updatedAt, email, password, username));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find account.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DomainError> deleteAccount(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT_BY_ID)) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete account.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DomainError> deletePrivilege(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRIVILEGE_BY_ID)) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete privilege.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

}
