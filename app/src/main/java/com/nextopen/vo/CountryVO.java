package com.nextopen.vo;

public class CountryVO {
    private long countryId;
    private String countryName;
    private String countryCode;
    private String isdCode;

    public CountryVO(long countryId, String countryName, String countryCode, String isdCode){
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.isdCode = isdCode;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public void setIsdCode(String isdCode) {
        this.isdCode = isdCode;
    }

}
