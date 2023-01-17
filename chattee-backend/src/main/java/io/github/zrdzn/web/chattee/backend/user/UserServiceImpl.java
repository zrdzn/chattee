package io.github.zrdzn.web.chattee.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.repository.save(user);
    }

    @Override
    public Optional<User> getUser(long id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<User> getUser(String email) {
        return this.repository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return this.repository.findAll();
    }

    @Override
    public void removeUser(long id) {
        if (this.repository.findById(id).isPresent()) {
            this.repository.deleteById(id);
        }
    }

}
