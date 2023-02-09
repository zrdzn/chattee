package io.github.zrdzn.web.chattee.backend.user.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.user.User;
import io.github.zrdzn.web.chattee.backend.user.UserRepository;
import io.github.zrdzn.web.chattee.backend.user.UserPrivilege;
import io.github.zrdzn.web.chattee.backend.web.security.Privilege;
import panda.std.Blank;
import panda.std.Result;

public class PostgresUserRepository implements UserRepository {

    private static final String INSERT_USER = "insert into users (email, password, username) values (?, ?, ?);";
    private static final String INSERT_PRIVILEGE = "insert into users_privileges (user_id, privilege) values (?, ?);";

    private static final String SELECT_ALL_USERS = "select id, email, password, username, created_at, updated_at from users;";
    private static final String SELECT_ALL_PRIVILEGES = "select id, user_id, privilege from users_privileges;";

    private static final String SELECT_USER_BY_ID = "select username, email, password, created_at, updated_at from users where id = ?;";
    private static final String SELECT_USER_BY_EMAIL = "select id, username, password, created_at, updated_at from users where email = ?;";
    private static final String SELECT_PRIVILEGE_BY_ID = "select user_id, privilege from users_privileges where id = ?;";
    private static final String SELECT_PRIVILEGE_BY_USER_ID = "select id, privilege from users_privileges where user_id = ?;";

    private static final String DELETE_USER_BY_ID = "delete from users where id = ?;";
    private static final String DELETE_PRIVILEGE_BY_ID = "delete from users_privileges where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresUserRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<User, Exception> saveUser(User user) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_USER)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());

                statement.executeUpdate();

                return user;
            }
        });
    }

    @Override
    public Result<UserPrivilege, Exception> savePrivilege(UserPrivilege privilege) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_PRIVILEGE)) {
                statement.setLong(1, privilege.getUserId());
                statement.setString(2, privilege.getPrivilege().name());

                statement.executeUpdate();

                return privilege;
            }
        });
    }

    @Override
    public Result<List<User>, Exception> listAllUsers() {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS)) {
                List<User> users = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    long id = result.getLong("id");
                    String email = result.getString("email");
                    String password = result.getString("password");
                    String username = result.getString("username");
                    Instant createdAt = result.getTimestamp("created_at").toInstant();
                    Instant updatedAt = result.getTimestamp("updated_at").toInstant();

                    users.add(new User(id, email, password, username, createdAt, updatedAt));
                }

                return users;
            }
        });
    }

    @Override
    public Result<List<UserPrivilege>, Exception> listAllPrivileges() {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PRIVILEGES)) {
                List<UserPrivilege> privileges = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    long id = result.getLong("id");
                    long userId = result.getLong("user_id");
                    Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                    privileges.add(new UserPrivilege(id, userId, privilege));
                }

                return privileges;
            }
        });
    }

    @Override
    public Result<Optional<User>, Exception> findUserById(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID)) {
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

                return Optional.of(new User(id, email, password, username, createdAt, updatedAt));
            }
        });
    }

    @Override
    public Result<Optional<User>, Exception> findUserByEmail(String email) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {
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

                return Optional.of(new User(id, email, password, username, createdAt, updatedAt));
            }
        });
    }

    @Override
    public Result<Optional<UserPrivilege>, Exception> findPrivilegeById(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGE_BY_ID)) {
                statement.setLong(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                long userId = result.getLong("user_id");
                Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                return Optional.of(new UserPrivilege(id, userId, privilege));
            }
        });
    }

    @Override
    public Result<List<UserPrivilege>, Exception> findPrivilegesByUserId(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGE_BY_USER_ID)) {
                statement.setLong(1, id);

                List<UserPrivilege> privileges = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    long recordId = result.getLong("id");
                    Privilege privilege = Privilege.valueOf(result.getString("privilege"));

                    privileges.add(new UserPrivilege(recordId, id, privilege));
                }

                return privileges;
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteUser(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_USER_BY_ID)) {
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
