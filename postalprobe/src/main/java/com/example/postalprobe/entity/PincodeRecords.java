package com.example.postalprobe.entity;

import com.example.postalprobe.utility.BooleanConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class PincodeRecords {
    @EmbeddedId
    private PincodePrimaryKey pincodePrimaryKey;

    private String circleName;
    private String regionName;
    private String officeType;

    @Convert(converter = BooleanConverter.class)
    private Boolean delivery;

    private String stateName;

    public PincodeRecords() {
    }

    public PincodeRecords(PincodePrimaryKey pincodePrimaryKey, String circleName, String regionName, String officeType, Boolean delivery, String stateName) {
        this.pincodePrimaryKey = pincodePrimaryKey;
        this.circleName = circleName;
        this.regionName = regionName;
        this.officeType = officeType;
        this.delivery = delivery;
        this.stateName = stateName;
    }

    public PincodePrimaryKey getPincodePrimaryKey() {
        return pincodePrimaryKey;
    }

    public void setPincodePrimaryKey(PincodePrimaryKey pincodePrimaryKey) {
        this.pincodePrimaryKey = pincodePrimaryKey;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getOfficeType() {
        return officeType;
    }

    public void setOfficeType(String officeType) {
        this.officeType = officeType;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOfficeName() {
        return this.pincodePrimaryKey.getOfficeName();
    }

    public int getPincode() {
        return this.pincodePrimaryKey.getPincode();
    }

    public String getDistrict() {
        return this.pincodePrimaryKey.getDistrict();
    }

    public String getDivisionName() {
        return this.pincodePrimaryKey.getDivisionName();
    }
}
