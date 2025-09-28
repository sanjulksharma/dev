package lld.practice.process.task.retry;

public class SimpleRetryPolicy implements RetryPolicy {

    private int maxAttempts;
    private int retryDelayMillis;

    @Override
    public boolean canRetry(int attempta) {
        return attempta < maxAttempts;
    }

    @Override
    public long getRetryDelayMillis(int attempt) {

        return retryDelayMillis;
    }
}
