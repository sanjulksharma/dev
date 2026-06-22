package lld.practice.splitswise.repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Generic CRUD abstraction. Services depend on this interface, not on the
 * in-memory implementation — letting us swap a real database later without
 * touching service code (Dependency Inversion Principle).
 */
public interface Repository<K, V> {
    V save(K id, V value);
    Optional<V> findById(K id);
    Collection<V> findAll();
    void deleteById(K id);
}
