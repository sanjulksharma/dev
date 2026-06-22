package lld.practice.infra.taskprocessor.processor;

import lld.practice.infra.taskprocessor.entity.Task;
import lld.practice.infra.taskprocessor.entity.TaskType;

public interface TaskProcessor<T extends Task> {
    void processTask(T task);
    TaskType getTaskType();
    void onSuccess(T task);
    void onFailure(T task);
}

