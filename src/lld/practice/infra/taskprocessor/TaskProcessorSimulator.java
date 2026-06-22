package lld.practice.infra.taskprocessor;

import lld.practice.infra.taskprocessor.processor.EmailTaskProcessor;
import lld.practice.infra.taskprocessor.processor.TaskProcessorRegistry;
import lld.practice.infra.taskprocessor.retry.SimpleRetryPolicy;
import lld.practice.infra.taskprocessor.service.TaskProcessorService;
import lld.practice.infra.taskprocessor.store.InMemoryTaskStore;
import lld.practice.infra.taskprocessor.store.TaskStore;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskProcessorSimulator {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        int noWorkers = 10;
        TaskStore taskStore = new InMemoryTaskStore();
        TaskProcessorService taskProcessorService = new TaskProcessorService(taskStore);
        registerProcessor(taskStore);

        List<Worker> taskWokers = new ArrayList<>();
        for (int i = 0; i < noWorkers; i++) {
            Worker worker = new Worker(taskProcessorService);
            taskWokers.add(worker);
            executor.execute(worker);
        }
    }

    private static void registerProcessor(TaskStore taskStore) {
        EmailTaskProcessor emailTaskProcessor = new EmailTaskProcessor(new SimpleRetryPolicy(), taskStore);
        TaskProcessorRegistry.getInstance().register(emailTaskProcessor);
    }
}
