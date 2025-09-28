package lld.practice.uber.client;

import lld.practice.uber.entity.Location;
import lld.practice.uber.entity.Ride;
import lld.practice.uber.entity.RideRequest;
import lld.practice.uber.entity.user.Driver;
import lld.practice.uber.entity.user.Rider;
import lld.practice.uber.service.DriverManagementService;
import lld.practice.uber.service.TripManagementService;
import lld.practice.uber.service.RiderManagementService;

public class Application {

    private DriverManagementService driverManagementService;
    private RiderManagementService riderManagementService;
    private TripManagementService tripManagementService;

    public void execute() {


        for (int i = 0; i < 10; i++) {
            Rider rider = new Rider();
            rider.setId("rider-" + i);
            rider.setName("Rider-" + i);
            riderManagementService.createUser(rider);
        }

        for (int i = 0; i < 2; i++) {
            Driver driver = new Driver();
            driver.setId("driver-" + i);
            driver.setName("Driver-" + i);
            driverManagementService.createUser(driver);
        }


        Location source = new Location();
        source.setLatitude("01-103-10-4213");
        source.setLongitude("01-104-10-4213");

        Location destination = new Location();
        destination.setLatitude("01-103-10-4213");
        destination.setLongitude("01-104-10-4213");


        RideRequest rideRequest = new RideRequest();
        rideRequest.setRiderId("rider-1");
        rideRequest.setSource(source);
        rideRequest.setDestination(destination);

        Ride ride = tripManagementService.requestRide(rideRequest);

        tripManagementService.acceptRide("driver-1", ride.getId());


    }

}
