package lld.practice.splitswise.exception;

/**
 * Base unchecked exception for all domain errors.
 *
 * <p>Using a typed hierarchy (rather than plain {@code RuntimeException}) lets
 * callers catch domain failures separately from infrastructure errors.
 */
public class SplitwiseException extends RuntimeException {
    public SplitwiseException(String message) {
        super(message);
    }

    public SplitwiseException(String message, Throwable cause) {
        super(message, cause);
    }
}
