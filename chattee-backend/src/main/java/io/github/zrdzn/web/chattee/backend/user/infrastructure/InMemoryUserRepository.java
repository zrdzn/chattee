package io.github.zrdzn.web.chattee.backend.user.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import io.github.zrdzn.web.chattee.backend.user.User;
import io.github.zrdzn.web.chattee.backend.user.UserPrivilege;
import io.github.zrdzn.web.chattee.backend.user.UserRepository;
import panda.std.Blank;
import panda.std.Result;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, UserPrivilege> privileges = new HashMap<>();

    @Override
    public Result<User, Exception> saveUser(User user) {
        this.users.put(user.getId(), user);
        return Result.ok(user);
    }

    public Result<UserPrivilege, Exception> savePrivilege(UserPrivilege privilege) {
        this.privileges.put(privilege.getId(), privilege);
        return Result.ok(privilege);
    }

    @Override
    public Result<List<User>, Exception> listAllUsers() {
        return Result.ok(new ArrayList<>(this.users.values()));
    }

    public Result<List<UserPrivilege>, Exception> listAllPrivileges() {
        return Result.ok(new ArrayList<>(this.privileges.values()));
    }

    @Override
    public Result<Optional<User>, Exception> findUserById(long id) {
        return Result.ok(Optional.ofNullable(this.users.get(id)));
    }

    @Override
    public Result<Optional<User>, Exception> findUserByEmail(String email) {
        return Result.ok(this.users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findAny());
    }

    @Override
    public Result<Optional<UserPrivilege>, Exception> findPrivilegeById(long id) {
        return Result.ok(Optional.ofNullable(this.privileges.get(id)));
    }

    @Override
    public Result<List<UserPrivilege>, Exception> findPrivilegesByUserId(long id) {
        return Result.ok(this.privileges.values().stream()
                .filter(privilege -> privilege.getUserId() == id)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<Blank, Exception> deleteUser(long id) {
        this.users.remove(id);
        return Result.ok();
    }

    @Override
    public Result<Blank, Exception> deletePrivilege(long id) {
        this.privileges.remove(id);
        return Result.ok();
    }

}
