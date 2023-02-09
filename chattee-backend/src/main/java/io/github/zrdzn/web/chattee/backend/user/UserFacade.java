package io.github.zrdzn.web.chattee.backend.user;

import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import panda.std.Blank;
import panda.std.Result;

public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public Result<User, HttpResponse> createUser(User user) {
        return this.userService.createUser(user);
    }

    public Result<UserPrivilege, HttpResponse> createPrivilege(UserPrivilege privilege) {
        return this.userService.createPrivilege(privilege);
    }

    public Result<List<User>, HttpResponse> getAllUsers() {
        return this.userService.getAllUsers();
    }

    public Result<List<UserPrivilege>, HttpResponse> getAllPrivileges() {
        return this.userService.getAllPrivileges();
    }

    public Result<Optional<User>, HttpResponse> getUser(long id) {
        return this.userService.getUser(id);
    }

    public Result<Optional<User>, HttpResponse> getUser(String email) {
        return this.userService.getUser(email);
    }

    public Result<Optional<UserPrivilege>, HttpResponse> getPrivilege(long id) {
        return this.userService.getPrivilege(id);
    }

    public Result<List<UserPrivilege>, HttpResponse> getPrivilegesByUserId(long id) {
        return this.userService.getPrivilegesByUserId(id);
    }

    public Result<Blank, HttpResponse> removeUser(long id) {
        return this.userService.removeUser(id);
    }

    public Result<Blank, HttpResponse> removePrivilege(long id) {
        return this.userService.removePrivilege(id);
    }

}
