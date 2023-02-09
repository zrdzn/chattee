package io.github.zrdzn.web.chattee.backend.plan.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.plan.Discussion;
import io.github.zrdzn.web.chattee.backend.plan.DiscussionRepository;
import io.github.zrdzn.web.chattee.backend.storage.postgres.PostgresStorage;
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

    public Result<Discussion, Exception> saveDiscussion(Discussion discussion) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT_DISCUSSION, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, discussion.getTitle());
                statement.setString(2, discussion.getDescription());
                statement.setLong(3, discussion.getAuthorId());
                statement.executeUpdate();

                long newId = statement.getGeneratedKeys().getLong("id");

                return new Discussion(newId, discussion.getTitle(), discussion.getDescription(), discussion.getAuthorId());
            }
        });
    }

    @Override
    public Result<List<Discussion>, Exception> listAllDiscussions() {
        return Result.supplyThrowing(() -> {
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

                return discussions;
            }
        });
    }

    @Override
    public Result<Optional<Discussion>, Exception> findDiscussionById(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_DISCUSSION_BY_ID)) {
                statement.setLong(1, id);

                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                String title = result.getString("title");
                String description = result.getString("description");
                long authorId = result.getLong("author_id");

                Discussion discussion = new Discussion(id, title, description, authorId);

                return Optional.of(discussion);
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteDiscussion(long id) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.postgresStorage.getHikariDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(DELETE_DISCUSSION_BY_ID)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

}
