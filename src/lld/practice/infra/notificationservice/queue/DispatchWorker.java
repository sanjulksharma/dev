package lld.practice.infra.notificationservice.queue;

import lld.practice.infra.notificationservice.service.DispatchService;

/**
 * Pulls messages from the queue and hands them to DispatchService.
 */
public class DispatchWorker implements Runnable {

    private final NotificationQueue queue;
    private final DispatchService dispatchService;
    private volatile boolean running = true;

    public DispatchWorker(NotificationQueue queue, DispatchService dispatchService) {
        this.queue = queue;
        this.dispatchService = dispatchService;
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                QueueMessage msg = queue.poll(200);
                if (msg != null) {
                    dispatchService.dispatch(msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                System.err.println("[Worker] error: " + e.getMessage());
            }
        }
    }
}
