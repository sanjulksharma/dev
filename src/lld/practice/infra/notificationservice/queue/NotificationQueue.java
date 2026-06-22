package lld.practice.infra.notificationservice.queue;

import lld.practice.infra.notificationservice.enums.Priority;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Multi-lane queue: one BlockingQueue per Priority. Workers poll P0 first, then P1, etc.
 * In production this would be Kafka topics partitioned by user_id.
 */
public class NotificationQueue {

    private final Map<Priority, BlockingQueue<QueueMessage>> lanes = new EnumMap<>(Priority.class);

    public NotificationQueue() {
        for (Priority p : Priority.values()) {
            lanes.put(p, new LinkedBlockingQueue<>());
        }
    }

    public void enqueue(QueueMessage msg, Priority priority) {
        lanes.get(priority).offer(msg);
    }

    /**
     * Poll lanes in priority order (P0 → P3). Block briefly per lane so workers don't spin.
     */
    public QueueMessage poll(long timeoutMs) throws InterruptedException {
        long perLane = Math.max(1, timeoutMs / Priority.values().length);
        for (Priority p : Priority.values()) {
            QueueMessage m = lanes.get(p).poll(perLane, TimeUnit.MILLISECONDS);
            if (m != null) return m;
        }
        return null;
    }

    public int size() {
        return lanes.values().stream().mapToInt(BlockingQueue::size).sum();
    }
}
