package io.github.zrdzn.web.chattee.backend.user;

import java.util.List;
import java.util.Optional;
import panda.std.Blank;
import panda.std.Result;

public interface UserRepository {

    Result<User, Exception> saveUser(User user);

    Result<UserPrivilege, Exception> savePrivilege(UserPrivilege privilege);

    Result<List<User>, Exception> listAllUsers();

    Result<List<UserPrivilege>, Exception> listAllPrivileges();

    Result<Optional<User>, Exception> findUserById(long id);

    Result<Optional<User>, Exception> findUserByEmail(String email);

    Result<Optional<UserPrivilege>, Exception> findPrivilegeById(long id);

    Result<List<UserPrivilege>, Exception> findPrivilegesByUserId(long id);

    Result<Blank, Exception> deleteUser(long id);

    Result<Blank, Exception> deletePrivilege(long id);

}
