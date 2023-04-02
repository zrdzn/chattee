package io.github.zrdzn.web.chattee.backend.discussion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

public class PostgresDiscussionRepository implements DiscussionRepository {

    private static final String INSERT_DISCUSSION = "insert into discussions (created_at, title, description, author_id) values (?, ?, ?, ?);";
    private static final String SELECT_ALL_DISCUSSIONS = "select id, created_at, title, description, author_id from discussions;";
    private static final String SELECT_DISCUSSION_BY_ID = "select created_at, title, description, author_id from discussions where id = ?;";
    private static final String DELETE_DISCUSSION_BY_ID = "delete from discussions where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresDiscussionRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    public Result<Discussion, DiscussionError> saveDiscussion(DiscussionCreateRequest discussionCreateRequest, long authorId) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_DISCUSSION, Statement.RETURN_GENERATED_KEYS)) {
            Instant createdAt = Instant.now();

            statement.setTimestamp(1, Timestamp.from(createdAt));
            statement.setString(2, discussionCreateRequest.getTitle());
            statement.setString(3, discussionCreateRequest.getDescription());
            statement.setLong(4, authorId);
            statement.executeUpdate();

            long id = 0L;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }

            return Result.ok(
                    new Discussion(
                            id,
                            createdAt,
                            discussionCreateRequest.getTitle(),
                            discussionCreateRequest.getDescription(),
                            authorId
                    )
            );
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.UNIQUE_VIOLATION.getState())) {
                return Result.error(DiscussionError.ALREADY_EXISTS);
            } else if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(DiscussionError.INVALID_ACCOUNT_ID);
            }

            Logger.error(exception, "Could not save discussion.");
            return Result.error(DiscussionError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Discussion>, DiscussionError> listAllDiscussions() {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DISCUSSIONS)) {
            List<Discussion> discussions = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong("id");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                String title = result.getString("title");
                String description = result.getString("description");
                long authorId = result.getLong("author_id");

                Discussion discussion = new Discussion(id, createdAt, title, description, authorId);
                discussions.add(discussion);
            }

            return Result.ok(discussions);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not list discussions.");
            return Result.error(DiscussionError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Discussion, DiscussionError> findDiscussionById(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DISCUSSION_BY_ID)) {
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(DiscussionError.NOT_EXISTS);
            }

            String title = result.getString("title");
            Instant createdAt = result.getTimestamp("created_at").toInstant();
            String description = result.getString("description");
            long authorId = result.getLong("author_id");

            Discussion discussion = new Discussion(id, createdAt, title, description, authorId);

            return Result.ok(discussion);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find discussion.");
            return Result.error(DiscussionError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, DiscussionError> deleteDiscussion(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DISCUSSION_BY_ID)) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete discussion.");
            return Result.error(DiscussionError.SQL_EXCEPTION);
        }
    }

}
