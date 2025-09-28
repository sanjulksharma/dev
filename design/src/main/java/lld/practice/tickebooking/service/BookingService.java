package lld.practice.tickebooking.service;

import lld.practice.tickebooking.entity.Journey;
import lld.practice.tickebooking.entity.Ticket;
import lld.practice.tickebooking.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookingService {

    private static final BookingService INSTANCE = new BookingService();
    private final Map<String, Journey> journeys;
    private final Map<String, Ticket> tickets;
    private final Map<String, User> users;
    private final Map<String, ReentrantLock> locks;

    private BookingService() {
        journeys = new HashMap<>();
        tickets = new HashMap<>();
        users = new HashMap<>();
        locks = new ConcurrentHashMap<>();
    }

    public static BookingService getInstance() {
        return INSTANCE;
    }

    public Ticket reserveTicketAndInitiatePayment(String journeyId, Integer seatNumber, String userId) {
        Lock lock = getLock(journeyId, seatNumber);
        lock.lock();
        try {
            Journey journey = journeys.get(journeyId);
            if (journey == null) {
                throw new IllegalArgumentException("Journey does not exist " + journeyId);
            }
            Map<Integer, String> seatMap = journey.getSeatStatus();
            String seatStatus = seatMap.get(seatNumber);
            if (!seatStatus.equals("Available")) {
                throw new IllegalArgumentException("Seat number " + seatNumber + " is not available, for journey " + journeyId);
            }
            seatMap.put(seatNumber, "Reserved");
            Ticket ticket = Ticket.of(journeyId, userId, seatNumber, 100);
            tickets.put(ticket.getId(), ticket);
            PaymentService.getInstance().initiatePayment(ticket.getId());
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public Ticket bookTicket(String ticketId, String paymentId) {
        Ticket ticket = tickets.get(ticketId);
        Lock lock = getLock(ticket.getJourneyId(), ticket.getSeatNumber());
        lock.lock();

        try {
            ticket.setPaymentId(paymentId);
            Journey journey = journeys.get(ticket.getJourneyId());
            journey.getSeatStatus().put(ticket.getSeatNumber(), "Booked");
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    private Lock getLock(String journeyId, Integer searNumber) {
        return locks.computeIfAbsent(journeyId + "_" + searNumber, k -> new ReentrantLock());
    }

}
