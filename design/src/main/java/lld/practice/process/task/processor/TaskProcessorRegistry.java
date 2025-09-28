package lld.practice.process.task.processor;

import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskType;

import java.util.EnumMap;

public class TaskProcessorRegistry {
    private static final TaskProcessorRegistry INSTANCE = new TaskProcessorRegistry();
    private final EnumMap<TaskType, TaskProcessor<? extends Task>> processorMap;

    private TaskProcessorRegistry() {
        processorMap = new EnumMap<>(TaskType.class);
    }

    public static TaskProcessorRegistry getInstance() {
        return INSTANCE;
    }

    public void register(TaskProcessor<? extends Task> taskProcessor) {
        processorMap.put(taskProcessor.getTaskType(), taskProcessor);
    }

    @SuppressWarnings("unchecked")
    public <T extends Task> TaskProcessor<T> getProcessor(TaskType taskType) {
        return (TaskProcessor<T>) processorMap.get(taskType);
    }

}
