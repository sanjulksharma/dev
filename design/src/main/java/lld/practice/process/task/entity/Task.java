package lld.practice.process.task.entity;

public interface Task {
    String getId();

    long getScheduledTime();

    void setScheduledTime(long scheduledTime);

    TaskStatus getStatus();

    void setStatus(TaskStatus taskStatus);

    TaskType getTaskType();

    int getRetryCount();

    void setRetryCount(int retryCount);
}
