package lld.practice.infra.taskprocessor;

import lld.practice.infra.taskprocessor.service.TaskProcessorService;

import java.util.Date;

public class Worker implements Runnable {
    private final TaskProcessorService taskProcessorService;
    private boolean running = true;

    public Worker(TaskProcessorService taskProcessorService) {
        this.taskProcessorService = taskProcessorService;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("Worker Running " + new Date());
            taskProcessorService.processTask(10);
        }
    }

    public void stop() {
        running = false;
    }
}
