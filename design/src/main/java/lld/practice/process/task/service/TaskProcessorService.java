package lld.practice.process.task.service;

import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskStatus;
import lld.practice.process.task.processor.TaskProcessor;
import lld.practice.process.task.processor.TaskProcessorRegistry;
import lld.practice.process.task.store.TaskStore;

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
