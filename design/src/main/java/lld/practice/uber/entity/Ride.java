package lld.practice.uber.entity;

import java.util.UUID;

public class Ride {

    private String id;
    private VehicleDetails vehicleDetails;
    private String driverId;
    private String riderId;
    private RideStatus rideStatus;
    private long startTime;
    private long endTime;
    private long distance;
    private long price;
    private Location source;
    private Location destination;

    public Ride() {
    }

    public Ride (RideRequest rideRequest) {

    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public VehicleDetails getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleDetails vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
    }

    public static class Builder {
        private RideRequest rideRequest;

        public Builder rideRequest(RideRequest rideRequest) {
            this.rideRequest = rideRequest;
            return this;
        }

        public Ride build() {
            Ride ride = new Ride();
            ride.setId(UUID.randomUUID().toString());
            ride.setRiderId(rideRequest.getRiderId());
            ride.setSource(rideRequest.getSource());
            ride.setDestination(rideRequest.getDestination());
            ride.setRideStatus(RideStatus.REQUESTED);
            return ride;
        }
    }
}
