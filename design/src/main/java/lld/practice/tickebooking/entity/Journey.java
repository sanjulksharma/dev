package lld.practice.tickebooking.entity;

import java.util.Map;

public class Journey {

    private String id;
    private String source;
    private String destination;
    private Long startTime;
    private Long endTime;
    private Boolean houseFull;
    private Map<Integer, String> seatStatus;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getHouseFull() {
        return houseFull;
    }

    public void setHouseFull(Boolean houseFull) {
        this.houseFull = houseFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Integer, String> getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(Map<Integer, String> seatStatus) {
        this.seatStatus = seatStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
