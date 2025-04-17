package com.example.PostalProbe.controller;

import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.*;
import com.example.PostalProbe.service.DeliveryService;
import com.example.PostalProbe.service.PincodeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/pincode-api/delivery_controller")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private PincodeService pincodeService;

    @Operation(summary = "Returns the Delivery Status on entering a Pincode")
    @GetMapping("/delivery/pincode/{pincode}")
    public ResponseEntity<?> getDeliveryStatusByPincode(@PathVariable int pincode) {
        String deliveryStatus = deliveryService.getDeliveryStatusByPincode(pincode);
        if (deliveryStatus == null) {
            return ResponseEntity.status(404).body("No record found for this pincode");
        }
        String message = "Delivery".equalsIgnoreCase(deliveryStatus)
                ? "Delivery Services Available"
                : "Delivery Services Not Available for the entered Pincode";
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Returns the Delivery Status on entering an Office Name")
    @GetMapping("/delivery/officeName/{officeName}")
    public ResponseEntity<?> getDeliveryStatusByOfficeName(@PathVariable String officeName) {
        List<Pincode> pincodes = deliveryService.getDeliveryStatusByOfficeName(officeName);
        if (pincodes.isEmpty()) {
            return ResponseEntity.status(404).body("No record found for this Office");
        } else if (pincodes.size() == 1) {
            String deliveryStatus = pincodes.get(0).getDelivery();
            String message = "Delivery".equalsIgnoreCase(deliveryStatus)
                    ? "Delivery Services Available"
                    : "Delivery Services Not Available for the entered Office";
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.ok(
                    pincodes.stream().map(p -> {
                        String deliveryStatus = p.getDelivery();
                        String message = "Delivery".equalsIgnoreCase(deliveryStatus)
                                ? "Delivery Services Available"
                                : "Delivery Services Not Available";
                        return new Object() {
                            public int pincode = p.getPincode();
                            public String officeName = p.getOfficeName();
                            public String delivery = message;
                        };
                    }).toList());
        }
    }

    @Operation(summary = "Returns the Delivery Status on entering the Primary Key")
    @GetMapping ("/delivery/primary-key")
    public ResponseEntity<?> getDeliveryStatusForPrimaryKey(@RequestBody PincodePrimaryKey primaryKey) {
        String deliveryStatus = deliveryService.getDeliveryStatusForPrimaryKey(primaryKey);
        if (deliveryStatus == null) {
            return ResponseEntity.status(404).body("No record found for the provided composite key");
        }
        return ResponseEntity.ok(deliveryStatus);
    }

    @Operation(summary = "Stops delivery (sets to 'Non Delivery') for all pincodes in a specified region")
    @PutMapping("/stop-delivery/region/{regionName}")
    public ResponseEntity<Map<String, UUID>> stopDeliveryForRegion(@PathVariable String regionName) {
        try{
            UUID transactionId = deliveryService.stopDeliveryForRegion(regionName);
            Map<String, UUID> response = new HashMap<>();
            response.put("transactionId", transactionId);
            return ResponseEntity.ok(response);
        } catch(RegionDoesNotExistException e){
            Map<String, UUID> errorResponse = new HashMap<>(); // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null); // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, UUID> errorResponse = new HashMap<>(); // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null); // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, UUID> errorResponse = new HashMap<>();  // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null);  // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Rollback a delivery status change for a Region")
    @PutMapping("/rollback-delivery/region/{transactionId}")
    public ResponseEntity<String> rollbackStopDeliveryForRegion(@PathVariable UUID transactionId) {
        try {
            deliveryService.rollbackStopDeliveryForRegion(transactionId);
            return ResponseEntity.ok("Delivery status change rolled back.");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during rollback: " + e.getMessage());
        }
    }

    @Operation(summary = "Starts delivery for all pincodes in a specified region")
    @PutMapping("/start-delivery/region/{regionName}")
    public ResponseEntity<String> startDeliveryForRegion(@PathVariable String regionName) {
        try{
            deliveryService.startDeliveryForRegion(regionName);
            return ResponseEntity.ok("Delivery started for all pincodes in the region: " + regionName);
        } catch(RegionDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Operation(summary = "Stops delivery (sets to 'Non Delivery') for all pincodes in a specified state")
    /**
     * Endpoint to stop delivery for a state.  Returns a transaction ID.
     *
     * @param stateName The name of the state.
     * @return A ResponseEntity containing the transaction ID.
     */
    @PutMapping("/stop-delivery/state/{stateName}")
    public ResponseEntity<Map<String, UUID>> stopDeliveryForState(@PathVariable String stateName) {
        try {
            UUID transactionId = deliveryService.stopDeliveryForState(stateName);
            Map<String, UUID> response = new HashMap<>();
            response.put("transactionId", transactionId);
            return ResponseEntity.ok(response);
        } catch (StateDoesNotExistException e) {
            Map<String, UUID> errorResponse = new HashMap<>(); // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null); // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, UUID> errorResponse = new HashMap<>();  // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null);  // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Rollback a delivery status change for a State")
    /**
     * Endpoint to rollback a delivery status change.
     *
     * @param transactionId The ID of the transaction to roll back.
     * @return A ResponseEntity indicating the result of the rollback.
     */
    @PutMapping("/rollback-delivery/state/{transactionId}")
    public ResponseEntity<String> rollbackStopDeliveryForState(@PathVariable UUID transactionId) {
        try {
            deliveryService.rollbackStopDeliveryForState(transactionId);
            return ResponseEntity.ok("Delivery status change rolled back.");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during rollback: " + e.getMessage());
        }
    }

    @Operation(summary = "Starts delivery for all pincodes in a specified state")
    @PutMapping("/start-delivery/state/{stateName}")
    public ResponseEntity<String> startDeliveryForState(@PathVariable String stateName) {
        try{
            deliveryService.startDeliveryForState(stateName);
            return ResponseEntity.ok("Delivery started for all pincodes in state: " + stateName);
        } catch(StateDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //Division
    @Operation(summary = "Stops delivery (sets to 'Non Delivery') for all pincodes in a specified division")
    @PutMapping("/stop-delivery/division/{divisionName}")
    public ResponseEntity<Map<String, UUID>> stopDeliveryForDivision(@PathVariable String divisionName) {
        try{
            UUID transactionId = deliveryService.stopDeliveryForDivision(divisionName);
            Map<String, UUID> response = new HashMap<>();
            response.put("transactionId", transactionId);
            return ResponseEntity.ok(response);
        } catch(DivisionDoesNotExistException e){
            Map<String, UUID> errorResponse = new HashMap<>(); // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null); // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }  catch (MultipleOccurancesException e) {
            Map<String, UUID> errorResponse = new HashMap<>();  // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null);  // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/rollback-delivery/division/{transactionId}")
    public ResponseEntity<String> rollbackStopDeliveryForDivision(@PathVariable UUID transactionId) {
        try {
            deliveryService.rollbackStopDeliveryForDivision(transactionId);
            return ResponseEntity.ok("Delivery status change rolled back.");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during rollback: " + e.getMessage());
        }
    }

    @Operation(summary = "Starts delivery for all pincodes in a specified division")
    @PutMapping("/start-delivery/division/{divisionName}")
    public ResponseEntity<String> startDeliveryForDivision(@PathVariable String divisionName) {
        try{
            deliveryService.startDeliveryForDivision(divisionName);
            return ResponseEntity.ok("Delivery started for all pincodes in division: " + divisionName);
        } catch(DivisionDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MultipleOccurancesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //District
    @Operation(summary = "Stops delivery (sets to 'Non Delivery') for all pincodes in a specified district")
    @PutMapping("/stop-delivery/district/{district}")
    public ResponseEntity<Map<String, UUID>> stopDeliveryForDistrict(@PathVariable String districtName) {
        try{
            UUID transactionId = deliveryService.stopDeliveryForDistrict(districtName);
            Map<String, UUID> response = new HashMap<>();
            response.put("transactionId", transactionId);
            return ResponseEntity.ok(response);
        } catch(DistrictDoesNotExistException e){
            Map<String, UUID> errorResponse = new HashMap<>(); // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null); // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch(MultipleOccurancesException e){
            Map<String, UUID> errorResponse = new HashMap<>();  // Changed to Map<String, UUID>
            errorResponse.put("error", (UUID) null);  // Changed to return null UUID.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/rollback-delivery/district/{transactionId}")
    public ResponseEntity<String> rollbackStopDeliveryForDistrict(@PathVariable UUID transactionId) {
        try {
            deliveryService.rollbackStopDeliveryForDistrict(transactionId);
            return ResponseEntity.ok("Delivery status change rolled back.");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during rollback: " + e.getMessage());
        }
    }

    @Operation(summary = "Starts delivery for all pincodes in a specified district")
    @PutMapping("/start-delivery/district/{district}")
    public ResponseEntity<String> startDeliveryForDistrict(@PathVariable String district) {
        try{
            deliveryService.startDeliveryForDistrict(district);
            return ResponseEntity.ok("Delivery started for all pincodes in district: " + district);
        } catch(DistrictDoesNotExistException e){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(MultipleOccurancesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}
