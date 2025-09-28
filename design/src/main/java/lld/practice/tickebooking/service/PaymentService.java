package lld.practice.tickebooking.service;


import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaymentService {

    private static final PaymentService INSTANCE = new PaymentService();
    private final ScheduledExecutorService executor;

    private PaymentService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static PaymentService getInstance() {
        return INSTANCE;
    }

    public void initiatePayment(String ticketId) {
        executor.schedule(() -> BookingService.getInstance().bookTicket(ticketId, UUID.randomUUID().toString()),
                1, TimeUnit.SECONDS);
    }
}
