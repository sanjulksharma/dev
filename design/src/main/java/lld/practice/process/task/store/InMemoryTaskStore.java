package lld.practice.process.task.store;

import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTaskStore<T extends Task> implements TaskStore<T> {
    private final Map<String, T> messages;

    public InMemoryTaskStore() {
        this.messages = new ConcurrentHashMap<>();
    }

    @Override
    public T fetchTaskById(String id) {
        return messages.get(id);
    }

    @Override
    public void saveTask(T task) {
        messages.put(task.getId(), task);
    }

    @Override
    public void deleteTask(String id) {
        messages.remove(id);
    }

    @Override
    public T updateTask(T task) {
        return messages.put(task.getId(), task);
    }

    @Override
    public List<T> fetchTaskToProcess(int limit) {
        List<T> messagesToProcess = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, T> entry : messages.entrySet()) {
            if (entry.getValue().getStatus() != TaskStatus.NEW || entry.getValue().getScheduledTime() > now) {
                continue;
            }
            limit--;
            if (limit < 0) {
                break;
            }
            T message = entry.getValue();
            message.setStatus(TaskStatus.PICKED);
        }
        return messagesToProcess;
    }
}
