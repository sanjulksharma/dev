package lld.practice.uber.service;

import lld.practice.uber.entity.Ride;
import lld.practice.uber.entity.user.Driver;
import lld.practice.uber.entity.user.UserStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DriverManagementService implements UserManagementService<Driver> {

    private final Map<String, Driver> driversById = new HashMap<>();

    @Override
    public Driver createUser(Driver driver) {
        // do validations/authentication etc other tasks here
        if (driver.getId() == null || driver.getId().isBlank()) {
            driver.setId(UUID.randomUUID().toString());
        } else if (driversById.containsKey(driver.getId())) {
            throw new IllegalArgumentException("Duplicate driver id: " + driver.getId());
        }

        return driversById.put(driver.getId(), driver);
    }

    @Override
    public Driver updateUser(Driver driver) {
        if (driver.getId() == null || driver.getId().isBlank()) {
            throw new IllegalArgumentException("Driver id can not be blank");
        }

        // do update task
        return driversById.put(driver.getId(), driver);
    }

    @Override
    public boolean deleteUser(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("Driver id can not be blank");
        }

        if (!driversById.containsKey(driverId)) {
            throw new IllegalArgumentException("Driver does not exist: " + driverId);
        }
        driversById.remove(driverId);
        // persist in db
        return true;
    }

    @Override
    public Driver getUser(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("Driver id can not be blank");
        }
        return driversById.get(driverId);
    }

    public List<Driver> getAllDriversInStatus(UserStatus status) {
        return driversById.values().stream().filter(t -> status == t.getStatus()).toList();
    }

}
