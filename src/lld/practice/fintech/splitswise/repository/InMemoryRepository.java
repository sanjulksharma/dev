package lld.practice.fintech.splitswise.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory {@link Repository}. Good enough for tests/demos;
 * production swaps it for a JDBC- or JPA-backed implementation behind the same
 * interface.
 */
public class InMemoryRepository<K, V> implements Repository<K, V> {

    private final Map<K, V> store = new ConcurrentHashMap<>();

    @Override
    public V save(K id, V value) {
        store.put(id, value);
        return value;
    }

    @Override
    public Optional<V> findById(K id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Collection<V> findAll() {
        return store.values();
    }

    @Override
    public void deleteById(K id) {
        store.remove(id);
    }
}
