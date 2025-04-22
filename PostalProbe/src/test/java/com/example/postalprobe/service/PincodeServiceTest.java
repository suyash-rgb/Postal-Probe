package com.example.postalprobe.service;

import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.repository.PincodeRepository;
import com.example.PostalProbe.service.PincodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PincodeServiceTest {

    @Mock
    private PincodeRepository pincodeRepository;

    @InjectMocks
    private PincodeService pincodeService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this); //Initializes Mocks
    }

    @Test
    public void testGetPincodesByDistrict() {
        // 1. Arrange
        // Create a sample list of Pincodes
        List<Pincode> expectedPincodes = Arrays.asList(
                new Pincode(), // You'll need to populate this with actual test data
                new Pincode()  // You'll need to populate this with actual test data
        );

        String testDistrict = "Test District";

        // "Stub" the behavior of the pincodeRepository.
        when(pincodeRepository.findByPincodePrimaryKeyDistrict(testDistrict)).thenReturn(expectedPincodes);

        // 2. Act
        // Call the service method we're testing
        String test_officeType = "B.O";
        List<Pincode> actualPincodes = pincodeService.getOfficesByDistrict(testDistrict, test_officeType);

        // 3. Assert
        // Verify that the result matches our expectation.
        assertEquals(expectedPincodes, actualPincodes);
    }
}

