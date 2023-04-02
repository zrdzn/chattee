package io.github.zrdzn.web.chattee.backend.discussion.post;

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

public class PostgresPostRepository implements PostRepository {

    private static final String INSERT_POST = "insert into discussions_posts (created_at, content, author_id, discussion_id) values (?, ?, ?, ?);";
    private static final String SELECT_ALL_POSTS_BY_DISCUSSION_ID = "select id, created_at, content, author_id from discussions_posts where discussion_id = ?;";
    private static final String SELECT_POST_BY_ID = "select created_at, content, author_id, discussion_id from discussions_posts where id = ?;";
    private static final String DELETE_POST_BY_ID = "delete from discussions_posts where id = ?;";

    private final PostgresStorage postgresStorage;

    public PostgresPostRepository(PostgresStorage postgresStorage) {
        this.postgresStorage = postgresStorage;
    }

    public Result<Post, PostError> savePost(PostCreateRequest postCreateRequest, long authorId, long discussionId) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_POST, Statement.RETURN_GENERATED_KEYS)) {
            Instant createdAt = Instant.now();

            statement.setTimestamp(1, Timestamp.from(createdAt));
            statement.setString(2, postCreateRequest.getContent());
            statement.setLong(3, authorId);
            statement.setLong(4, discussionId);
            statement.executeUpdate();

            long id = 0L;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }

            return Result.ok(
                    new Post(
                            id,
                            createdAt,
                            postCreateRequest.getContent(),
                            authorId,
                            discussionId
                    )
            );
        } catch (SQLException exception) {
            String state = exception.getSQLState();
            if (state.equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                return Result.error(PostError.ID_TARGETS_INVALID_RECORD);
            }

            Logger.error(exception, "Could not save post.");
            return Result.error(PostError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<List<Post>, PostError> findPostsByDiscussionId(long discussionId) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_POSTS_BY_DISCUSSION_ID)) {
            statement.setLong(1, discussionId);

            List<Post> posts = new ArrayList<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong("id");
                Instant createdAt = result.getTimestamp("created_at").toInstant();
                String content = result.getString("content");
                long authorId = result.getLong("author_id");

                posts.add(new Post(id, createdAt, content, authorId, discussionId));
            }

            return Result.ok(posts);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find posts.");
            return Result.error(PostError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Post, PostError> findPostById(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_POST_BY_ID)) {
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Result.error(PostError.NOT_EXISTS);
            }

            Instant createdAt = result.getTimestamp("created_at").toInstant();
            String content = result.getString("content");
            long authorId = result.getLong("author_id");
            long discussionId = result.getLong("discussion_id");

            Post post = new Post(id, createdAt, content, authorId, discussionId);

            return Result.ok(post);
        } catch (SQLException exception) {
            Logger.error(exception, "Could not find post.");
            return Result.error(PostError.SQL_EXCEPTION);
        }
    }

    @Override
    public Result<Blank, PostError> deletePostById(long id) {
        try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_POST_BY_ID)) {
            statement.setLong(1, id);
            statement.executeUpdate();

            return Result.ok();
        } catch (SQLException exception) {
            Logger.error(exception, "Could not delete post.");
            return Result.error(PostError.SQL_EXCEPTION);
        }
    }

}
