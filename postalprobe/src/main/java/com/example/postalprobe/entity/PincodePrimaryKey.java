package com.example.postalprobe.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PincodePrimaryKey implements Serializable {

    private String officeName;
    private Integer pincode;
    private String district;
    private String divisionName;

    public PincodePrimaryKey() {

    }

    public PincodePrimaryKey(String officeName, Integer pincode, String district, String divisionName) {
        this.officeName = officeName;
        this.pincode = pincode;
        this.district = district;
        this.divisionName = divisionName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PincodePrimaryKey that = (PincodePrimaryKey) o;
        return Objects.equals(officeName, that.officeName) && Objects.equals(pincode, that.pincode) && Objects.equals(district, that.district) && Objects.equals(divisionName, that.divisionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(officeName, pincode, district, divisionName);
    }

}
