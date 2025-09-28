package lld.practice.uber.entity.user;

public class IdentityDetails {

    private String identityType;
    private IdentityType identityNumber;

    public IdentityType getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(IdentityType identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }
}
