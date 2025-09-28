package lld.practice.process.task.retry;

public interface RetryPolicy {
    boolean canRetry(int attempt);
    long getRetryDelayMillis(int attempt);
}
