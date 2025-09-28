package lld.practice.tickebooking.entity;

import java.util.UUID;

public class Ticket {

    private String id;
    private String journeyId;
    private String userId;
    private String paymentId;
    private Integer seatNumber;
    private Integer amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public static Ticket of(String journeyId, String userId, Integer seatNumber, Integer amount) {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setJourneyId(journeyId);
        ticket.setUserId(userId);
        ticket.setSeatNumber(seatNumber);
        return ticket;
    }
}
