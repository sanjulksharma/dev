package lld.practice.uber.service;

import lld.practice.uber.entity.*;
import lld.practice.uber.entity.user.Driver;
import lld.practice.uber.entity.user.UserStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripManagementService {

    private DriverManagementService driverManagementService;
    private final int RIDE_SEARCH_TIMEOUT = 120_000;
    private final long MINIMUM_DISTANCE_TO_OFFER_RIDE = 500;

    private Map<String, Ride> activeRides = new HashMap<>();

    public Ride requestRide(RideRequest rideRequest) {
        Ride ride = new Ride.Builder().rideRequest(rideRequest).build();

        activeRides.put(ride.getId(), ride);
        long startTime = System.currentTimeMillis();

        while (RideStatus.ACCEPTED == ride.getRideStatus()) {
            if (System.currentTimeMillis() - startTime > RIDE_SEARCH_TIMEOUT) {
                throw new RuntimeException("Cannot Find Ride, please try again after some time");
            }
            Driver driver = tryAssignRide(ride);
            if (driver != null) {
                ride.setRideStatus(RideStatus.ACCEPTED);
                ride.setDriverId(driver.getId());
                ride.setVehicleDetails(driver.getVehicleDetails());
                ride.setStartTime(System.currentTimeMillis());
            }
        }
        return ride;
    }

    public boolean acceptRide(String driverId, String rideId) {
        Driver driver = driverManagementService.getUser(driverId);
        //basic validation of user status
        if (driver.getStatus() != UserStatus.AVAILABLE) {
            throw new IllegalArgumentException("Cannot accept multiple trips at a time ");
        }

        Ride ride = activeRides.get(rideId);
        if (ride == null || ride.getRideStatus() != RideStatus.REQUESTED) {
            System.out.println("Not able to accept ride : " + rideId );
            return false;
        }
        ride.setRideStatus(RideStatus.ACCEPTED);
        ride.setDriverId(driver.getId());
        ride.setVehicleDetails(driver.getVehicleDetails());
        ride.setStartTime(System.currentTimeMillis());
        activeRides.remove(rideId);

        driver.setStatus(UserStatus.OCCUPIED);
        driverManagementService.updateUser(driver);
        
        return true;
    }

    public Driver tryAssignRide(Ride ride) {
        Driver toReturn = null;
        List<Driver> drivers = driverManagementService.getAllDriversInStatus(UserStatus.AVAILABLE);
        for (Driver driver : drivers) {
            long distance = driver.getLocation().distanceFrom(ride.getSource());
            if (distance < MINIMUM_DISTANCE_TO_OFFER_RIDE) {
                offerRide(driver, ride);
            }
        }
        return toReturn;
    }


    private void offerRide(Driver driver, Ride ride) {
        // logic to send pop up on driver's ui
    }
}
