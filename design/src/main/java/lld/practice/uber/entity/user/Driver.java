package lld.practice.uber.entity.user;

import lld.practice.uber.entity.VehicleDetails;

public class Driver extends User {

    private IdentityDetails identityDetails;
    private VehicleDetails vehicleDetails;
    private String licenseNumber;

    public IdentityDetails getIdentityDetails() {
        return identityDetails;
    }

    public void setIdentityDetails(IdentityDetails identityDetails) {
        this.identityDetails = identityDetails;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public VehicleDetails getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleDetails vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public UserType getType() {
        return UserType.DRIVER;
    }
}
