package lld.practice.taskqueue;

/*
Distributed Task Queue System - LLD skeleton in Java
Contains interfaces and classes to illustrate design: Task, TaskHandler, TaskMessage,
TaskStatus, RetryPolicy, TaskStore (persistence), Queue (abstraction), Scheduler, Worker,
Dispatcher, MonitorService. This is a conceptual skeleton — integration points (Redis/Kafka/DB)
are represented as interfaces so they can be swapped with real implementations.
*/

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

// --- Core data models --------------------------------------------------
public class DistributedTaskQueueSystem {

    // Task payload & metadata
    public static class TaskMessage {
        public final String taskId;
        public final String type; // e.g., EMAIL, REPORT, ORDER
        public final Map<String, Object> payload;
        public final int priority; // higher -> processed earlier
        public Instant scheduledAt; // for delayed tasks
        public final int maxRetries;
        public int attempts = 0;
        public TaskStatus status = TaskStatus.PENDING;
        public Instant createdAt = Instant.now();

        public TaskMessage(String taskId, String type, Map<String,Object> payload, int priority, Instant scheduledAt, int maxRetries){
            this.taskId = taskId;
            this.type = type;
            this.payload = payload == null ? Collections.emptyMap() : new HashMap<>(payload);
            this.priority = priority;
            this.scheduledAt = scheduledAt == null ? Instant.now() : scheduledAt;
            this.maxRetries = Math.max(0, maxRetries);
        }

        @Override
        public String toString(){
            return String.format("Task[id=%s,type=%s,prio=%d,scheduled=%s,status=%s,attempts=%d]",
                    taskId,type,priority,scheduledAt,status,attempts);
        }
    }

    public enum TaskStatus { PENDING, RESERVED, RUNNING, SUCCESS, FAILED, DEAD }

    // --- Retry policy -------------------------------------------------
    public static class RetryPolicy {
        private final int maxRetries;
        private final long baseBackoffMillis;

        public RetryPolicy(int maxRetries, long baseBackoffMillis){
            this.maxRetries = maxRetries;
            this.baseBackoffMillis = baseBackoffMillis;
        }

        public long nextBackoffMillis(int attempt){
            // exponential backoff with jitter
            long backoff = baseBackoffMillis * (1L << Math.max(0, attempt-1));
            long jitter = ThreadLocalRandom.current().nextLong(0, baseBackoffMillis);
            return backoff + jitter;
        }

        public boolean canRetry(int attempt){
            return attempt < maxRetries;
        }
    }

    // --- Persistence abstraction (DB) ----------------------------------
    public interface TaskStore {
        void save(TaskMessage task);
        Optional<TaskMessage> fetch(String taskId);
        void update(TaskMessage task);
        List<TaskMessage> fetchPendingTasks(int limit);
        void markDead(String taskId, String reason);
    }

    // Simple in-memory store for demonstration / unit tests
    public static class InMemoryTaskStore implements TaskStore {
        private final ConcurrentMap<String, TaskMessage> store = new ConcurrentHashMap<>();

        @Override
        public void save(TaskMessage task) {
            store.put(task.taskId, task);
        }

        @Override
        public Optional<TaskMessage> fetch(String taskId) {
            return Optional.ofNullable(store.get(taskId));
        }

        @Override
        public void update(TaskMessage task) {
            store.put(task.taskId, task);
        }

        @Override
        public List<TaskMessage> fetchPendingTasks(int limit) {
            List<TaskMessage> out = new ArrayList<>();
            for (TaskMessage t : store.values()){
                if ((t.status == TaskStatus.PENDING || t.status == TaskStatus.FAILED) && t.scheduledAt.isBefore(Instant.now())){
                    out.add(t);
                    if (out.size() >= limit) break;
                }
            }
            out.sort(Comparator.comparingInt(tm -> -tm.priority));
            return out;
        }

        @Override
        public void markDead(String taskId, String reason) {
            TaskMessage t = store.get(taskId);
            if (t != null){
                t.status = TaskStatus.DEAD;
                update(t);
            }
        }
    }

    // --- Queue abstraction (could be Redis list, Kafka topic, etc.) ----
    public interface TaskQueue {
        void push(TaskMessage task);
        Optional<TaskMessage> poll(long timeout, TimeUnit unit);
        // for ordered priority-aware queue, implementations may vary
    }

    // Simple in-memory priority queue
    public static class InMemoryPriorityQueue implements TaskQueue {
        private final PriorityBlockingQueue<TaskMessage> pq = new PriorityBlockingQueue<>(1024,
                Comparator.comparingInt((TaskMessage t) -> -t.priority)
                        .thenComparing(t -> t.scheduledAt));

        @Override
        public void push(TaskMessage task) {
            pq.put(task);
        }

        @Override
        public Optional<TaskMessage> poll(long timeout, TimeUnit unit) {
            try {
                TaskMessage t = pq.poll(timeout, unit);
                return Optional.ofNullable(t);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
    }

    // --- Task Handler -------------------------------------------------
    public interface TaskHandler {
        void handle(TaskMessage task) throws Exception;
    }

    // Registry of handlers
    public static class HandlerRegistry {
        private final Map<String, TaskHandler> handlers = new ConcurrentHashMap<>();
        public void register(String type, TaskHandler handler) { handlers.put(type, handler); }
        public Optional<TaskHandler> get(String type) { return Optional.ofNullable(handlers.get(type)); }
    }

    // --- Dispatcher / TaskProcessor ----------------------------------------
    public static class Producer {
        private final TaskStore store;
        private final TaskQueue queue;

        public Producer(TaskStore store, TaskQueue queue){ this.store = store; this.queue = queue; }

        public String submit(String type, Map<String, Object> payload, int priority, Instant scheduledAt, int maxRetries){
            String id = UUID.randomUUID().toString();
            TaskMessage t = new TaskMessage(id, type, payload, priority, scheduledAt, maxRetries);
            store.save(t);
            // If scheduled time in future, we might keep in DB only and scheduler will enqueue later
            if (!t.scheduledAt.isAfter(Instant.now())){
                queue.push(t);
            }
            return id;
        }
    }

    // --- Scheduler (for delayed tasks) --------------------------------
    public static class Scheduler {
        private final TaskStore store;
        private final TaskQueue queue;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public Scheduler(TaskStore store, TaskQueue queue){ this.store = store; this.queue = queue; }

        public void start(){
            scheduler.scheduleAtFixedRate(this::pollAndEnqueue, 0, 1, TimeUnit.SECONDS);
        }

        public void stop(){ scheduler.shutdownNow(); }

        private void pollAndEnqueue(){
            List<TaskMessage> due = store.fetchPendingTasks(100);
            for (TaskMessage t : due){
                if (!t.scheduledAt.isAfter(Instant.now())){
                    queue.push(t);
                }
            }
        }
    }

    // --- Worker -------------------------------------------------------
    public static class Worker implements Runnable {
        private final String workerId;
        private final TaskStore store;
        private final TaskQueue queue;
        private final HandlerRegistry registry;
        private final RetryPolicy retryPolicy;
        private volatile boolean running = true;

        public Worker(String workerId, TaskStore store, TaskQueue queue, HandlerRegistry registry, RetryPolicy retryPolicy){
            this.workerId = workerId; this.store = store; this.queue = queue; this.registry = registry; this.retryPolicy = retryPolicy;
        }

        @Override
        public void run(){
            while (running && !Thread.currentThread().isInterrupted()){
                Optional<TaskMessage> opt = queue.poll(1, TimeUnit.SECONDS);
                if (!opt.isPresent()) continue;
                TaskMessage t = opt.get();
                // mark reserved
                t.status = TaskStatus.RESERVED;
                store.update(t);

                // check scheduledAt
                if (t.scheduledAt.isAfter(Instant.now())){
                    // re-enqueue later
                    queue.push(t);
                    continue;
                }

                Optional<TaskHandler> handlerOpt = registry.get(t.type);
                if (!handlerOpt.isPresent()){
                    t.status = TaskStatus.FAILED;
                    store.update(t);
                    continue;
                }

                TaskHandler handler = handlerOpt.get();
                t.status = TaskStatus.RUNNING;
                t.attempts += 1;
                store.update(t);

                try {
                    handler.handle(t);
                    t.status = TaskStatus.SUCCESS;
                    store.update(t);
                } catch (Exception ex){
                    t.status = TaskStatus.FAILED;
                    store.update(t);
                    if (retryPolicy.canRetry(t.attempts)){
                        long backoff = retryPolicy.nextBackoffMillis(t.attempts);
                        // schedule retry by setting scheduledAt
                        t.scheduledAt = Instant.now().plusMillis(backoff);
                        store.update(t);
                        // In production, you might publish to a delayed queue instead
                    } else {
                        store.markDead(t.taskId, ex.getMessage());
                    }
                }
            }
        }

        public void stop(){ running = false; }
    }

    // --- MonitorService ------------------------------------------------
    public static class MonitorService {
        private final TaskStore store;
        public MonitorService(TaskStore store){ this.store = store; }
        public Map<TaskStatus, Long> summary(){
            Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);
            for (TaskMessage t : ((InMemoryTaskStore)store).store.values()){
                counts.merge(t.status, 1L, Long::sum);
            }
            return counts;
        }
    }

    // --- Example handler implementations -------------------------------
    public static class EmailHandler implements TaskHandler {
        @Override
        public void handle(TaskMessage task) throws Exception {
            // simulate sending email
            System.out.println("[EmailHandler] Sending email for task " + task.taskId + " payload=" + task.payload);
            // simulate occasional failure
            if (ThreadLocalRandom.current().nextInt(10) == 0) throw new RuntimeException("smtp-failure");
        }
    }

    public static class ReportHandler implements TaskHandler {
        @Override
        public void handle(TaskMessage task) throws Exception {
            System.out.println("[ReportHandler] Generating report " + task.taskId);
        }
    }

    // --- Main demonstration --------------------------------------------
    public static void main(String[] args) throws Exception {
        InMemoryTaskStore store = new InMemoryTaskStore();
        InMemoryPriorityQueue queue = new InMemoryPriorityQueue();
        HandlerRegistry registry = new HandlerRegistry();
        registry.register("EMAIL", new EmailHandler());
        registry.register("REPORT", new ReportHandler());

        Producer producer = new Producer(store, queue);
        Scheduler scheduler = new Scheduler(store, queue);
        scheduler.start();

        RetryPolicy retryPolicy = new RetryPolicy(3, 200);

        // start few worker threads
        List<Worker> workers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i=0;i<4;i++){
            Worker w = new Worker("worker-"+i, store, queue, registry, retryPolicy);
            Thread t = new Thread(w);
            threads.add(t); workers.add(w);
            t.start();
        }

        // processTask tasks
        for (int i=0;i<20;i++){
            Map<String,Object> payload = new HashMap<>();
            payload.put("email","user"+i+"@example.com");
            producer.submit("EMAIL", payload, i%5, Instant.now(), 3);
        }

        // let it run for a while
        Thread.sleep(5000);

        // print summary
        MonitorService monitor = new MonitorService(store);
        System.out.println("Summary: " + monitor.summary());

        // shutdown
        for (Worker w : workers) w.stop();
        scheduler.stop();
        for (Thread t : threads) t.interrupt();
    }
}
