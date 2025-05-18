package com.example.postalprobe.service;

import com.example.PostalProbe.repository.PincodeRepository;
import com.example.PostalProbe.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    PincodeRepository pincodeRepository;

    @InjectMocks
    DeliveryService deliveryService;

    @Test
    void getDeliveryStatusByPincode() {
        //System.out.println("My First test!");

        int pincode = 454001;
        deliveryService.getDeliveryStatusByPincode(pincode);
    }

    @Test
    void getDeliveryStatusByOfficeName() {
    }

    @Test
    void getDeliveryStatusForPrimaryKey() {
    }
}