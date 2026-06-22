package lld.practice.infra.taskprocessor.service;

import lld.practice.infra.taskprocessor.entity.Task;
import lld.practice.infra.taskprocessor.entity.TaskStatus;
import lld.practice.infra.taskprocessor.processor.TaskProcessor;
import lld.practice.infra.taskprocessor.processor.TaskProcessorRegistry;
import lld.practice.infra.taskprocessor.store.TaskStore;

import java.util.List;

public class TaskProcessorService {
    private final TaskStore<Task> taskStore;
    private final TaskProcessorRegistry taskProcessorRegistry = TaskProcessorRegistry.getInstance();

    public TaskProcessorService(TaskStore<Task> taskStore) {
        this.taskStore = taskStore;
    }

    public void processTask(int limit) {
        List<Task> tasksToProcess = taskStore.fetchTaskToProcess(limit);
        for (Task task : tasksToProcess) {
            TaskProcessor taskProcessor = taskProcessorRegistry.getProcessor(task.getTaskType());
            if (taskProcessor == null) {
                task.setStatus(TaskStatus.FAILED);
                taskStore.saveTask(task);
                continue;
            }
            taskProcessor.processTask(task);
        }
    }

    public void saveTask(Task task) {
        taskStore.saveTask(task);
    }
}
