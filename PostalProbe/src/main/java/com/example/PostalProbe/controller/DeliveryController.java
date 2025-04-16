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

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<String> stopDeliveryForRegion(@PathVariable String regionName) {
        try{
            deliveryService.stopDeliveryForRegion(regionName);
            return ResponseEntity.ok("Delivery status set to 'Non Delivery' for all pincodes in the region: " + regionName);
        } catch(RegionDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    @PutMapping("/stop-delivery/state/{stateName}")
    public ResponseEntity<String> stopDeliveryForState(@PathVariable String stateName) {
        try{
            deliveryService.stopDeliveryForState(stateName);
            return ResponseEntity.ok("Delivery status set to 'Non Delivery' for all pincodes in the state: " + stateName);
        } catch(StateDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    public ResponseEntity<String> stopDeliveryForDivision(@PathVariable String divisionName) {
        try{
            deliveryService.stopDeliveryForDivision(divisionName);
            return ResponseEntity.ok("Delivery status set to 'Non Delivery' for all pincodes in the division: " + divisionName);
        } catch(DivisionDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }  catch (MultipleOccurancesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
    public ResponseEntity<String> stopDeliveryForDistrict(@PathVariable String district) {
        try{
            deliveryService.stopDeliveryForDistrict(district);
            return ResponseEntity.ok("Delivery status set to 'Non Delivery' for all pincodes in district: " + district);
        } catch(DistrictDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(MultipleOccurancesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
