package lld.practice.process.task.processor;

import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskType;

public interface TaskProcessor<T extends Task> {
    void processTask(T task);
    TaskType getTaskType();
    void onSuccess(T task);
    void onFailure(T task);
}

