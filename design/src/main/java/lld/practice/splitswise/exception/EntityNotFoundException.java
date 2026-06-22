package lld.practice.splitswise.exception;

/** Thrown when an entity lookup by id misses. */
public class EntityNotFoundException extends SplitwiseException {
    public EntityNotFoundException(String entityType, String id) {
        super(entityType + " not found: " + id);
    }
}
