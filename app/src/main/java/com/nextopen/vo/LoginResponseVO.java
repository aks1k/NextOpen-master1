package com.nextopen.vo;

public class LoginResponseVO {
    private long userID;
    private String mobileDeviceType;
    private String mobileCountry;
    private String mobilePhoneLocal;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getMobileDeviceType() {
        return mobileDeviceType;
    }

    public void setMobileDeviceType(String mobileDeviceType) {
        this.mobileDeviceType = mobileDeviceType;
    }

    public String getMobileCountry() {
        return mobileCountry;
    }

    public void setMobileCountry(String mobileCountry) {
        this.mobileCountry = mobileCountry;
    }

    public String getMobilePhoneLocal() {
        return mobilePhoneLocal;
    }

    public void setMobilePhoneLocal(String mobilePhoneLocal) {
        this.mobilePhoneLocal = mobilePhoneLocal;
    }
}
