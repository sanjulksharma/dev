package lld.practice.splitswise.services;

import lld.practice.splitswise.exception.EntityNotFoundException;
import lld.practice.splitswise.repository.Repository;
import lld.practice.splitswise.user.User;

import java.util.Collection;

/**
 * Service for user lifecycle. Depends on the {@link Repository} abstraction
 * rather than a concrete map (Dependency Inversion Principle), so we can
 * swap storage without touching this class.
 */
public class UserService {

    private final Repository<String, User> userRepository;

    public UserService(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String email, String name) {
        User user = new User(email, name);
        return userRepository.save(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    public void updateEmail(String userId, String email) {
        User user = getUser(userId);
        user.setEmail(email);
        userRepository.save(userId, user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }
}
