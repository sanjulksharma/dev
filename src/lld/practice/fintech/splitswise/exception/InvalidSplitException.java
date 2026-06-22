package lld.practice.fintech.splitswise.exception;

/** Thrown when an expense's splits do not satisfy the strategy's invariants. */
public class InvalidSplitException extends SplitwiseException {
    public InvalidSplitException(String message) {
        super(message);
    }
}
