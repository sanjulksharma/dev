package lld.practice.infra.taskprocessor.store;

import lld.practice.infra.taskprocessor.entity.Task;

import java.util.List;

public interface TaskStore<T extends Task> {
    T fetchTaskById(String id);
    void saveTask(T task);
    void deleteTask(String id);
    T updateTask(T task);
    List<T> fetchTaskToProcess(int limit);
}
