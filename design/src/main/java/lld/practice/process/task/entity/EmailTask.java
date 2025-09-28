package lld.practice.process.task.entity;

import java.util.UUID;

public class EmailTask implements Task {
    private final String id;
    private final String to;
    private final String subject;
    private final String body;
    private long scheduledTime;
    private TaskStatus taskStatus;
    private int retryCount;

    public EmailTask(String to, String subject, String body) {
        this(to, subject, body, System.currentTimeMillis(), TaskStatus.NEW);
    }

    public EmailTask(String to, String subject, String body, long scheduledTime, TaskStatus taskStatus) {
        this.id = UUID.randomUUID().toString();
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.scheduledTime = scheduledTime;
        this.taskStatus = taskStatus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public TaskStatus getStatus() {
        return taskStatus;
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EMAIL;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }
}
