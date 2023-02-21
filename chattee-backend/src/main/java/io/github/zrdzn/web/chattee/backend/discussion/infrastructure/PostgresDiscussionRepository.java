package io.github.zrdzn.web.chattee.backend.discussion.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.discussion.Discussion;
import io.github.zrdzn.web.chattee.backend.discussion.DiscussionRepository;
import io.github.zrdzn.web.chattee.backend.shared.DomainError;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

public class PostgresDiscussionRepository implements DiscussionRepository {

    private static final String INSERT_DISCUSSION = "insert into discussions (title, description, author_id) values (?, ?, ?);";
    private static final String SELECT_ALL_DISCUSSIONS = "select id, title, description, author_id from discussions;";
    private static final String SELECT_DISCUSSION_BY_ID = "select title, description, author_id from discussions where id = ?;";
    private static final String DELETE_DISCUSSION_BY_ID = "delete from discussions where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresDiscussionRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    public Result<Discussion, DomainError> saveDiscussion(Discussion discussion) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_DISCUSSION, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, discussion.getTitle());
            statement.setString(2, discussion.getDescription());
            statement.setLong(3, discussion.getAuthorId());
            statement.executeUpdate();

            long newId = statement.getGeneratedKeys().getLong("id");

            return Result.ok(new Discussion(newId, discussion.getTitle(), discussion.getDescription(), discussion.getAuthorId()));
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(DomainError.DISCUSSION_ALREADY_EXISTS);
            } else if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DomainError.ACCOUNT_INVALID_ID);
            }

            Logger.error(exception, "Could not save discussion.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Discussion>, DomainError> listAllDiscussions() {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DISCUSSIONS)) {
            List<Discussion> discussions = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong("id");
                String title = result.getString("title");
                String description = result.getString("description");
                long authorId = result.getLong("author_id");

                Discussion discussion = new Discussion(id, title, description, authorId);
                discussions.add(discussion);
            }

            return Result.ok(discussions);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not list discussions.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Discussion, DomainError> findDiscussionById(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DISCUSSION_BY_ID)) {
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DomainError.DISCUSSION_NOT_EXISTS);
            }

            String title = result.getString("title");
            String description = result.getString("description");
            long authorId = result.getLong("author_id");

            Discussion discussion = new Discussion(id, title, description, authorId);

            return Result.ok(discussion);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find discussion.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DomainError> deleteDiscussion(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DISCUSSION_BY_ID)) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete discussion.");
            return Result.error(DomainError.SQL_EXCEPTION);
        }
    }

}
