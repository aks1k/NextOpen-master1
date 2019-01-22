package com.nextopen.vo;

public class LoginRequestVO {
    private String mobileDeviceType;
    private String mobileCountry;
    private String mobilePhoneLocal;
    private String facebookId;

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

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
