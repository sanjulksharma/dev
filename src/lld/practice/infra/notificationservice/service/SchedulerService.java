package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.dto.NotificationRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Delays a NotificationRequest until scheduled_at, then hands it to IntakeService.
 * In production: persistent ZSET (Redis) or a delayed-message broker.
 */
public class SchedulerService {

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2, r -> {
                Thread t = new Thread(r, "scheduler");
                t.setDaemon(true);
                return t;
            });
    private final IntakeService intakeService;

    public SchedulerService(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    public void schedule(NotificationRequest req) {
        if (req.getScheduledAt() == null || req.getScheduledAt().isBefore(Instant.now())) {
            intakeService.submit(req);
            return;
        }
        long delayMs = Duration.between(Instant.now(), req.getScheduledAt()).toMillis();
        scheduler.schedule(() -> intakeService.submit(req), delayMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
