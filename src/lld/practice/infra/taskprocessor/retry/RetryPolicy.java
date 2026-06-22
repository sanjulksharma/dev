package lld.practice.infra.taskprocessor.retry;

public interface RetryPolicy {
    boolean canRetry(int attempt);
    long getRetryDelayMillis(int attempt);
}
