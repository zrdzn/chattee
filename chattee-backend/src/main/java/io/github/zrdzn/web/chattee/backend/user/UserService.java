package io.github.zrdzn.web.chattee.backend.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getUser(long id);

    Optional<User> getUser(String email);

    List<User> getAllUsers();

    void removeUser(long id);

}
