package lld.practice.infra.notificationservice.repository;

import lld.practice.infra.notificationservice.model.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserRepository {
    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>();

    public void save(User user) { store.put(user.getId(), user); }

    public Optional<User> findById(String id) { return Optional.ofNullable(store.get(id)); }
}
