package com.example.PostalProbe.DTOs;

public class CheckDeliveryStatusResponse {
    public String officeName;
    public String deliveryStatus;

    public CheckDeliveryStatusResponse(String officeName, String deliveryStatus) {
        this.officeName = officeName;
        this.deliveryStatus = deliveryStatus;
    }
}
