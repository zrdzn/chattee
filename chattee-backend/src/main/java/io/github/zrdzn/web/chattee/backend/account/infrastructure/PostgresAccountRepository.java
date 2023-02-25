package io.github.zrdzn.web.chattee.backend.account.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.AccountRepository;
import io.github.zrdzn.web.chattee.backend.account.AccountPrivilege;
import io.github.zrdzn.web.chattee.backend.web.security.Privilege;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

public class PostgresAccountRepository implements AccountRepository {

    private static final String INSERT_ACCOUNT = "insert into accounts (email, password, username) values (?, ?, ?);";
    private static final String INSERT_PRIVILEGE = "insert into accounts_privileges (account_id, privilege) values (?, ?);";

    private static final String SELECT_ALL_ACCOUNTS = "select id, email, password, username, created_at, updated_at from accounts;";

    private static final String SELECT_ACCOUNT_BY_ID = "select email, password, username, created_at, updated_at from accounts where id = ?;";
    private static final String SELECT_ACCOUNT_BY_EMAIL = "select id, password, username, created_at, updated_at from accounts where email = ?;";
    private static final String SELECT_PRIVILEGE_BY_ACCOUNT_ID = "select id, privilege from accounts_privileges where account_id = ?;";

    private static final String DELETE_ACCOUNT_BY_ID = "delete from accounts where id = ?;";
    private static final String DELETE_PRIVILEGE_BY_ID = "delete from accounts_privileges where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresAccountRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<Account, DomainError> saveAccount(Account account) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT)) {
            statement.setString(1, account.getEmail());
            statement.setString(2, account.getPassword());
            statement.setString(3, account.getUsername());

            statement.executeUpdate();

            return Result.ok(account);
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
    public Result<AccountPrivilege, DomainError> savePrivilege(AccountPrivilege privilege) {
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
                String email = result.getString("email");
                String password = result.getString("password");
                String username = result.getString("username");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                Instant updatedAt = result.getTimestamp("updated_at").toInstant();

                accounts.add(new Account(id, email, password, username, createdAt, updatedAt));
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

            String email = result.getString("email");
            String password = result.getString("password");
            String username = result.getString("username");
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            Instant updatedAt = result.getTimestamp("updated_at").toInstant();

            return Result.ok(new Account(id, email, password, username, createdAt, updatedAt));
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
            String password = result.getString("password");
            String username = result.getString("username");
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            Instant updatedAt = result.getTimestamp("updated_at").toInstant();

            return Result.ok(new Account(id, email, password, username, createdAt, updatedAt));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find account.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<AccountPrivilege>, DomainError> findPrivilegesByAccountId(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGE_BY_ACCOUNT_ID)) {
            statement.setLong(1, id);

            List<AccountPrivilege> privileges = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long recordId = result.getLong("id");
                Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                privileges.add(new AccountPrivilege(recordId, privilege));
            }

            return Result.ok(privileges);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find privileges.");
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
