package com.nextopen.vo;

public class KeyOperationRequestVO {
    private long userID;
    private String gateMajor;
    private String gateMinor;
    private String gateTime;
    private double userAltitude;
    private double userLatitude;
    private double longitude;
    private String userQRcode1;
    private String userQRcode2;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getGateMajor() {
        return gateMajor;
    }

    public void setGateMajor(String gateMajor) {
        this.gateMajor = gateMajor;
    }

    public String getGateMinor() {
        return gateMinor;
    }

    public void setGateMinor(String gateMinor) {
        this.gateMinor = gateMinor;
    }

    public String getGateTime() {
        return gateTime;
    }

    public void setGateTime(String gateTime) {
        this.gateTime = gateTime;
    }

    public double getUserAltitude() {
        return userAltitude;
    }

    public void setUserAltitude(double userAltitude) {
        this.userAltitude = userAltitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserQRcode1() {
        return userQRcode1;
    }

    public void setUserQRcode1(String userQRcode1) {
        this.userQRcode1 = userQRcode1;
    }

    public String getUserQRcode2() {
        return userQRcode2;
    }

    public void setUserQRcode2(String userQRcode2) {
        this.userQRcode2 = userQRcode2;
    }
}
