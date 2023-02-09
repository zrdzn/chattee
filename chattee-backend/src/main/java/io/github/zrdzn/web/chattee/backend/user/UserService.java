package io.github.zrdzn.web.chattee.backend.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import org.postgresql.util.PSQLState;
import org.tinylog.Logger;
import panda.std.Blank;
import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<User, HttpResponse> createUser(User user) {
        user.setPassword(BCrypt.withDefaults().hashToString(10, user.getPassword().toCharArray()));

        return this.userRepository.saveUser(user)
                .onError(error -> Logger.error(error, "Could not save the user."))
                .mapErr(error -> internalServerError("Could not create the user."));
    }

    public Result<UserPrivilege, HttpResponse> createPrivilege(UserPrivilege privilege) {
        return this.userRepository.savePrivilege(privilege)
                .onError(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return;
                        }
                    }

                    Logger.error(error, "Could not save the privilege.");
                })
                .mapErr(error -> {
                    if (error instanceof SQLException sqlException) {
                        if (sqlException.getSQLState().equalsIgnoreCase(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
                            return badRequest("'userId' does not target an existing record.");
                        }
                    }

                    return internalServerError("Could not create the privilege.");
                });
    }

    public Result<List<User>, HttpResponse> getAllUsers() {
        return this.userRepository.listAllUsers()
                .onError(error -> Logger.error(error, "Could not list all users."))
                .mapErr(error -> internalServerError("Could not retrieve all users."));
    }

    public Result<List<UserPrivilege>, HttpResponse> getAllPrivileges() {
        return this.userRepository.listAllPrivileges()
                .onError(error -> Logger.error(error, "Could not list all privileges."))
                .mapErr(error -> internalServerError("Could not retrieve all privileges."));
    }

    public Result<Optional<User>, HttpResponse> getUser(long id) {
        return this.userRepository.findUserById(id)
                .onError(error -> Logger.error(error, "Could not find an user."))
                .mapErr(error -> internalServerError("Could not retrieve an user."));
    }

    public Result<Optional<User>, HttpResponse> getUser(String email) {
        return this.userRepository.findUserByEmail(email)
                .onError(error -> Logger.error(error, "Could not find an user."))
                .mapErr(error -> internalServerError("Could not retrieve an user."));
    }

    public Result<Optional<UserPrivilege>, HttpResponse> getPrivilege(long id) {
        return this.userRepository.findPrivilegeById(id)
                .onError(error -> Logger.error(error, "Could not find a privilege."))
                .mapErr(error -> internalServerError("Could not retrieve a privilege."));
    }

    public Result<List<UserPrivilege>, HttpResponse> getPrivilegesByUserId(long id) {
        return this.userRepository.findPrivilegesByUserId(id)
                .onError(error -> Logger.error(error, "Could not find privileges."))
                .mapErr(error -> internalServerError("Could not retrieve privileges."));
    }

    public Result<Blank, HttpResponse> removeUser(long id) {
        return this.userRepository.deleteUser(id)
                .onError(error -> Logger.error(error, "Could not delete an user."))
                .mapErr(error -> internalServerError("Could not remove an user."));
    }

    public Result<Blank, HttpResponse> removePrivilege(long id) {
        return this.userRepository.deletePrivilege(id)
                .onError(error -> Logger.error(error, "Could not delete a privilege."))
                .mapErr(error -> internalServerError("Could not remove a privilege."));
    }

}
