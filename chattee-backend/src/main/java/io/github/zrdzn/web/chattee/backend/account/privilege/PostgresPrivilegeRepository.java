package io.github.zrdzn.web.chattee.backend.account.privilege;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Result;

public class PostgresPrivilegeRepository implements PrivilegeRepository {

    private static final String CREATE_PRIVILEGE = "insert into accounts_privileges (account_id, privilege) values (?, ?);";
    private static final String SELECT_PRIVILEGES_BY_ACCOUNT_ID = "select privilege from accounts_privileges where account_id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresPrivilegeRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    @Override
    public Result<List<Privilege>, PrivilegeError> createPrivileges(long accountId, List<RoutePrivilege> privileges) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_PRIVILEGE)) {
            List<Privilege> returnedPrivileges = new ArrayList<>();

            for (RoutePrivilege privilege : privileges) {
                statement.setLong(1, accountId);
                statement.setString(2, privilege.name());
                statement.addBatch();

                returnedPrivileges.add(new Privilege(accountId, privilege));
            }

            statement.executeBatch();

            return Result.ok(returnedPrivileges);
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(PrivilegeError.ALREADY_EXISTS);
            }

            Logger.error(exception, "Could not create privileges.");
            return Result.error(PrivilegeError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Privilege>, PrivilegeError> findPrivilegesByAccountId(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PRIVILEGES_BY_ACCOUNT_ID)) {
            statement.setLong(1, id);

            List<Privilege> privileges = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                RoutePrivilege routePrivilege = RoutePrivilege.valueOf(result.getString("privilege"));

                privileges.add(new Privilege(id, routePrivilege));
            }

            return Result.ok(privileges);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find privileges.");
            return Result.error(PrivilegeError.SQL_EXCEPTION);
        }
    }

}
