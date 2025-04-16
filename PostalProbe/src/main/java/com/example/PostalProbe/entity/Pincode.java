package com.example.PostalProbe.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import com.example.PostalProbe.utility.BooleanConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "pincode")
public class Pincode {

    @EmbeddedId
    //@ApiModelProperty(notes = "Composite primary key of the Pincode", required = true)
    private PincodePrimaryKey pincodePrimaryKey;

    //@ApiModelProperty(notes = "Name of the circle", example = "North Circle")
    @Column(name="CircleName")
    private String circleName;

    //@ApiModelProperty(notes = "Name of the region", example = "North Region")
    @Column(name="RegionName")
    private String regionName;

    //@ApiModelProperty(notes = "Type of office", example = "HO")
    @Column(name="OfficeType")
    private String officeType;

    //@ApiModelProperty(notes = "Delivery status", example = "true")
    @Column(name="Delivery")
    //@Convert(converter = BooleanConverter.class)
    private String delivery;

    //@ApiModelProperty(notes = "Name of the state", example = "Madhya Pradesh")
    @Column(name="StateName")
    private String stateName;

    public Pincode() {}

    public Pincode(PincodePrimaryKey pincodePrimaryKey, String circleName, String regionName, String officeType, String delivery, String stateName) {
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

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    // Adding getters from Embedded Id (Composite primary key)
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
