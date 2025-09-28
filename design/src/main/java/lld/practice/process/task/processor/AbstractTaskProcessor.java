package lld.practice.process.task.processor;

import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskStatus;
import lld.practice.process.task.retry.RetryPolicy;
import lld.practice.process.task.store.TaskStore;

public abstract class AbstractTaskProcessor<T extends Task> implements TaskProcessor<T> {
    protected final TaskStore<T> taskStore;
    private final RetryPolicy retryPolicy;

    protected AbstractTaskProcessor(RetryPolicy retryPolicy, TaskStore<T> taskStore) {
        this.retryPolicy = retryPolicy;
        this.taskStore = taskStore;
    }

    @Override
    public void processTask(T task) {
        try {
            doProcessTask(task);
            onSuccess(task);
        } catch (Exception e) {
            onFailure(task);
        } finally {
            taskStore.saveTask(task);
        }
    }

    protected abstract void doProcessTask(T task);

    @Override
    public void onFailure(T task) {
        if (retryPolicy.canRetry(task.getRetryCount())) {
            task.setStatus(TaskStatus.NEW);
            task.setRetryCount(task.getRetryCount() + 1);
            task.setScheduledTime(System.currentTimeMillis() + retryPolicy.getRetryDelayMillis(task.getRetryCount()));
        } else {
            task.setStatus(TaskStatus.FAILED);
        }
    }

    @Override
    public void onSuccess(T task) {
        task.setStatus(TaskStatus.SCHEDULED);
    }
}
