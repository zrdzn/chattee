package io.github.zrdzn.web.chattee.backend.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Result;

public class SqlWebOptionRepository implements WebOptionRepository {

    private static final String CREATE_OPTION = "insert into web_options (name, value) values (?, ?);";
    private static final String FIND_OPTION_BY_NAME = "select id, value from web_options where name = ?;";

    private final PostgresStorage postgresStorage;

    public SqlWebOptionRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    public Result<WebOption, WebOptionError> createOption(WebOptionCreateRequest optionCreateRequest) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_OPTION, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, optionCreateRequest.getName());
            statement.setString(2, optionCreateRequest.getValue());
            statement.executeUpdate();

            long id = 0L;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }

            return Result.ok(new WebOption(id, optionCreateRequest.getName(), optionCreateRequest.getValue()));
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(WebOptionError.ALREADY_EXISTS);
            }

            Logger.error(exception, "Could not create web option.");
            return Result.error(WebOptionError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<WebOption, WebOptionError> findOptionByName(String name) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_OPTION_BY_NAME)) {
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(WebOptionError.NOT_EXISTS);
            }

            long id = result.getLong("id");
            String value = result.getString("value");

            return Result.ok(new WebOption(id, name, value));
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find web option.");
            return Result.error(WebOptionError.SQL_EXCEPTION);
        }
    }

}
