package io.github.zrdzn.web.chattee.backend.account.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.account.Account;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.account.AccountRepository;
import io.github.zrdzn.web.chattee.backend.account.AccountPrivilege;
import io.github.zrdzn.web.chattee.backend.web.security.Privilege;
import panda.std.Blank;
import panda.std.Result;

public class PostgresAccountRepository implements AccountRepository {

    private static final String INSERT_ACCOUNT = "insert into accounts (email, password, username) values (?, ?, ?);";
    private static final String INSERT_PRIVILEGE = "insert into accounts_privileges (account_id, privilege) values (?, ?);";

    private static final String SELECT_ALL_ACCOUNTS = "select id, email, password, username, created_at, updated_at from accounts;";
    private static final String SELECT_ALL_PRIVILEGES = "select id, account_id, privilege from accounts_privileges;";

    private static final String SELECT_ACCOUNT_BY_ID = "select email, password, username, created_at, updated_at from accounts where id = ?;";
    private static final String SELECT_ACCOUNT_BY_EMAIL = "select id, password, username, created_at, updated_at from accounts where email = ?;";
    private static final String SELECT_PRIVILEGE_BY_ID = "select account_id, privilege from accounts_privileges where id = ?;";
    private static final String SELECT_PRIVILEGE_BY_USER_ID = "select id, privilege from accounts_privileges where account_id = ?;";

    private static final String DELETE_ACCOUNT_BY_ID = "delete from accounts where id = ?;";
    private static final String DELETE_PRIVILEGE_BY_ID = "delete from accounts_privileges where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresAccountRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<Account, Exception> saveAccount(Account account) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT)) {
                statement.setString(1, account.getEmail());
                statement.setString(2, account.getPassword());
                statement.setString(3, account.getUsername());

                statement.executeUpdate();

                return account;
            }
        });
    }

    @Override
    public Result<AccountPrivilege, Exception> savePrivilege(AccountPrivilege privilege) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_PRIVILEGE)) {
                statement.setLong(1, privilege.getAccountId());
                statement.setString(2, privilege.getPrivilege().name());

                statement.executeUpdate();

                return privilege;
            }
        });
    }

    @Override
    public Result<List<Account>, Exception> listAllAccounts() {
        return Result.supplyThrowing(() -> {
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

                return accounts;
            }
        });
    }

    @Override
    public Result<List<AccountPrivilege>, Exception> listAllPrivileges() {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PRIVILEGES)) {
                List<AccountPrivilege> privileges = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    long id = result.getLong("id");
                    long accountId = result.getLong("account_id");
                    Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                    privileges.add(new AccountPrivilege(id, accountId, privilege));
                }

                return privileges;
            }
        });
    }

    @Override
    public Result<Optional<Account>, Exception> findAccountById(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
                statement.setLong(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                String email = result.getString("email");
                String password = result.getString("password");
                String username = result.getString("username");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                Instant updatedAt = result.getTimestamp("updated_at").toInstant();

                return Optional.of(new Account(id, email, password, username, createdAt, updatedAt));
            }
        });
    }

    @Override
    public Result<Optional<Account>, Exception> findAccountByEmail(String email) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT_BY_EMAIL)) {
                statement.setString(1, email);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                long id = result.getLong("id");
                String password = result.getString("password");
                String username = result.getString("username");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                Instant updatedAt = result.getTimestamp("updated_at").toInstant();

                return Optional.of(new Account(id, email, password, username, createdAt, updatedAt));
            }
        });
    }

    @Override
    public Result<Optional<AccountPrivilege>, Exception> findPrivilegeById(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGE_BY_ID)) {
                statement.setLong(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                long accountId = result.getLong("account_id");
                Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                return Optional.of(new AccountPrivilege(id, accountId, privilege));
            }
        });
    }

    @Override
    public Result<List<AccountPrivilege>, Exception> findPrivilegesByAccountId(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGE_BY_USER_ID)) {
                statement.setLong(1, id);

                List<AccountPrivilege> privileges = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    long recordId = result.getLong("id");
                    Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                    privileges.add(new AccountPrivilege(recordId, id, privilege));
                }

                return privileges;
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteAccount(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT_BY_ID)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

    @Override
    public Result<Blank, Exception> deletePrivilege(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_PRIVILEGE_BY_ID)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

}
