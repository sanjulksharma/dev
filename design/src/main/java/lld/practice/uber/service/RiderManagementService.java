package lld.practice.uber.service;


import lld.practice.uber.entity.user.Rider;

import java.util.HashMap;
import java.util.Map;

public class RiderManagementService implements UserManagementService<Rider> {

    private final Map<String, Rider> ridersById = new HashMap<>();

    @Override
    public Rider createUser(Rider rider) {
        // validate and authenticate

        if (rider.getId() == null || rider.getId().isBlank()) {
            rider.setId(String.valueOf(System.currentTimeMillis()));
        } else if (ridersById.containsKey(rider.getId())) {
            throw new IllegalArgumentException("Rider already exists");
        }

        ridersById.put(rider.getId(), rider);
        return rider;
    }

    @Override
    public Rider updateUser(Rider rider) {
        if (rider.getId() == null || rider.getId().isBlank()) {
            throw new IllegalArgumentException("Rider id can not be blank");
        }
        ridersById.put(rider.getId(), rider);
        return rider;
    }

    @Override
    public boolean deleteUser(String riderId) {
        if (riderId == null || riderId.isBlank()) {
            throw new IllegalArgumentException("Rider id can not be blank");
        }
        ridersById.remove(riderId);
        return true;
    }

    @Override
    public Rider getUser(String riderId) {
        if (riderId == null || riderId.isBlank()) {
            throw new IllegalArgumentException("Rider id can not be blank");
        }
        return ridersById.get(riderId);
    }
}
