package lld.practice.infra.notificationservice.api;

import lld.practice.infra.notificationservice.channel.ChannelHandlerFactory;
import lld.practice.infra.notificationservice.dto.NotificationRequest;
import lld.practice.infra.notificationservice.dto.NotificationResponse;
import lld.practice.infra.notificationservice.model.DeliveryAttempt;
import lld.practice.infra.notificationservice.model.Template;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.model.UserPreference;
import lld.practice.infra.notificationservice.queue.DispatchWorker;
import lld.practice.infra.notificationservice.queue.NotificationQueue;
import lld.practice.infra.notificationservice.repository.DeliveryAttemptRepository;
import lld.practice.infra.notificationservice.repository.NotificationRepository;
import lld.practice.infra.notificationservice.repository.PreferenceRepository;
import lld.practice.infra.notificationservice.repository.TemplateRepository;
import lld.practice.infra.notificationservice.repository.UserRepository;
import lld.practice.infra.notificationservice.service.DeliveryTrackerService;
import lld.practice.infra.notificationservice.service.DispatchService;
import lld.practice.infra.notificationservice.service.IntakeService;
import lld.practice.infra.notificationservice.service.PreferenceService;
import lld.practice.infra.notificationservice.service.RateLimiter;
import lld.practice.infra.notificationservice.service.SchedulerService;
import lld.practice.infra.notificationservice.service.TemplateService;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton entry point. Wires all components and exposes a small API.
 */
public class NotificationFacade {

    private static volatile NotificationFacade INSTANCE;

    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final PreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    private final NotificationQueue queue;
    private final ChannelHandlerFactory channelHandlerFactory;

    private final TemplateService templateService;
    private final PreferenceService preferenceService;
    private final RateLimiter rateLimiter;
    private final DeliveryTrackerService trackerService;
    private final DispatchService dispatchService;
    private final IntakeService intakeService;
    private final SchedulerService schedulerService;

    private final List<Thread> workerThreads = new ArrayList<>();
    private final List<DispatchWorker> workers = new ArrayList<>();

    private NotificationFacade(int workerCount) {
        this.userRepository = new UserRepository();
        this.templateRepository = new TemplateRepository();
        this.preferenceRepository = new PreferenceRepository();
        this.notificationRepository = new NotificationRepository();
        this.deliveryAttemptRepository = new DeliveryAttemptRepository();

        this.queue = new NotificationQueue();
        this.channelHandlerFactory = new ChannelHandlerFactory();

        this.templateService = new TemplateService(templateRepository);
        this.preferenceService = new PreferenceService(preferenceRepository);
        this.rateLimiter = new RateLimiter(60); // 60/min default
        this.trackerService = new DeliveryTrackerService(deliveryAttemptRepository);

        this.dispatchService = new DispatchService(notificationRepository, userRepository,
                preferenceService, templateService, channelHandlerFactory, rateLimiter,
                trackerService, queue);
        this.intakeService = new IntakeService(notificationRepository, userRepository, queue);
        this.schedulerService = new SchedulerService(intakeService);

        startWorkers(workerCount);
    }

    public static NotificationFacade getInstance() {
        if (INSTANCE == null) {
            synchronized (NotificationFacade.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotificationFacade(4);
                }
            }
        }
        return INSTANCE;
    }

    private void startWorkers(int n) {
        for (int i = 0; i < n; i++) {
            DispatchWorker w = new DispatchWorker(queue, dispatchService);
            Thread t = new Thread(w, "dispatch-worker-" + i);
            t.setDaemon(true);
            workers.add(w);
            workerThreads.add(t);
            t.start();
        }
    }

    // ── Public API ──────────────────────────────────────────────

    public void registerUser(User user) { userRepository.save(user); }
    public void registerTemplate(Template t) { templateService.registerTemplate(t); }
    public void savePreference(UserPreference p) { preferenceService.savePreference(p); }
    public ChannelHandlerFactory getChannelHandlerFactory() { return channelHandlerFactory; }

    public NotificationResponse send(NotificationRequest req) {
        if (req.getScheduledAt() != null) {
            schedulerService.schedule(req);
            return new NotificationResponse(null,
                    lld.practice.infra.notificationservice.enums.NotificationStatus.PENDING, "Scheduled");
        }
        return intakeService.submit(req);
    }

    public List<DeliveryAttempt> getStatus(String notificationId) {
        return trackerService.getAttempts(notificationId);
    }

    public void shutdown() {
        for (DispatchWorker w : workers) w.stop();
        for (Thread t : workerThreads) t.interrupt();
        schedulerService.shutdown();
    }
}
