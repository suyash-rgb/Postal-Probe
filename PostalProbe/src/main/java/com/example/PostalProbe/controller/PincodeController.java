package com.example.PostalProbe.controller;

import com.example.PostalProbe.DTOs.PincodeUpdateRequest;
import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.StateDoesNotExistException;
import com.example.PostalProbe.repository.PincodeRepository;
import com.example.PostalProbe.service.PincodeService;
import com.example.PostalProbe.exceptions.CannotChangeDeliveryStatusException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pincode-api")
public class PincodeController {

    @Autowired
    private PincodeService pincodeService;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Operation(summary = "Greetings Message")
    @GetMapping("/")
    public ResponseEntity<String> greetingsMessage(){
        return ResponseEntity.ok("Postal Probe is a robust, efficient and an extensive API Solution designed to provide detailed postal information. ");
    }

    @Operation(summary = "Displays all pincode records (Pagination Applied)")
    @GetMapping("/getallpincoderecords")
    public ResponseEntity<Page<Pincode>> getAllPincodeRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Pincode> pincodeRecordsPage = pincodeService.getAllPincodes(page, size);
        return ResponseEntity.ok(pincodeRecordsPage);
    }

    @Operation(summary = "Search Pincode Records by entering a Pincode")
    @GetMapping("/searchbypincode/{pincode}")
    public ResponseEntity<?> searchByPincode(@PathVariable int pincode) {
        List<Pincode> pincodes = pincodeService.searchByPincode(pincode);
        if (pincodes.isEmpty()) {
            return ResponseEntity.status(404).body("No record found for this pincode");
        } else if (pincodes.size() == 1) {
            return ResponseEntity.ok(pincodes.get(0));
        } else {
            return ResponseEntity.ok(pincodes.stream().map(Pincode::getOfficeName).toList());
        }
    }

    @Operation(summary = "Search Pincode Records by entering a District name")
    @GetMapping("/searchbydistrict/{district}")
    public ResponseEntity<?> searchByDistrict(@PathVariable String district) {
        List<Pincode> pincodes = pincodeService.searchByDistrict(district);
        if (pincodes.isEmpty()) {
            return ResponseEntity.status(404).body("No record found for this district");
        } else {
            return ResponseEntity.ok(
                    pincodes.stream().map(p -> new Object() {
                        public String officeName = p.getOfficeName();
                        public int pincode = p.getPincode();
                        public String delivery = p.getDelivery();
                    }).toList());
        }
    }


    @Operation(summary = "Returns a list of Offices on entering the District and OfficeType")
    @GetMapping("/offices/district/{district}/type/{officeType}")
    public ResponseEntity<?> getOfficesByDistrictAndOfficeType( @PathVariable String district, @PathVariable String officeType) {
        List<Pincode> pincodes = pincodeService.getOfficesByDistrict(district, officeType);
        if (pincodes.isEmpty()) {
            return ResponseEntity.status(404).body("No offices found for the specified district and office type");
        }
        return ResponseEntity.ok(pincodes);
    }

    @Operation(summary = "Returns a list of Offices on entering the Division and OfficeType")
    @GetMapping("/offices/division/{division}/type/{officeType}")
    public ResponseEntity<?> getOfficesByDivisionAndOfficeType( @PathVariable String division, @PathVariable String officeType) {
        List<Pincode> pincodes = pincodeService.getOfficesByDivision(division, officeType);
        if (pincodes.isEmpty()) {
            return ResponseEntity.status(404).body("No offices found for the specified division and office type");
        }
        return ResponseEntity.ok(pincodes);
    }

    @Operation(summary = "Returns the type of Office on entering the name of Office")
    @GetMapping("/office-type/{officeName}")
    public ResponseEntity<?> getOfficeTypeForOfficeName( @PathVariable String officeName) {
        Optional<String> officeType = pincodeService.getOfficeTypeByOfficeName(officeName);
        if (!officeType.isPresent()) {
            return ResponseEntity.status(404).body("No office type found for the specified office name");
        }
        return ResponseEntity.ok(officeType.get());
    }

    @Operation(summary = "Updates Pincode Record on entering the Primary Key")
    @PutMapping("/update-pincode-record") // Removed path variables
    public ResponseEntity<?> updatePincodeRecord(
            @RequestParam int pincode,
            @RequestParam String officeName,
            @RequestParam String district,
            @RequestParam String divisionName,
            @RequestBody PincodeUpdateRequest updateRequest) {

        PincodePrimaryKey primaryKey = new PincodePrimaryKey();
        primaryKey.setPincode(pincode);
        primaryKey.setOfficeName(officeName);
        primaryKey.setDistrict(district);
        primaryKey.setDivisionName(divisionName);
        try {
            Pincode updatedPincode = pincodeService.updatePincode(primaryKey, updateRequest);
            return ResponseEntity.ok(updatedPincode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "Suggests matches to the OfficeName entered")
    @GetMapping("/search-office-name-starts-with")
    public ResponseEntity<?> searchOfficeNameStartsWith(@RequestParam("officeName") String officeName) {
        List<Pincode> results = pincodeRepository.findByOfficeNameStartingWith(officeName);

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (results.size() == 1) {
            return ResponseEntity.ok(results.get(0).getPincodePrimaryKey().getPincode());
        } else {
            return ResponseEntity.ok(results);
        }
    }

    @Operation(summary = "Returns all districts for a given state")
    @GetMapping("/districts/{stateName}")
    public ResponseEntity<?> getDistrictsForState(@PathVariable String stateName) {
        try {
            List<String> districts = pincodeService.getDistrictsByStateName(stateName);
            if (districts.isEmpty()) {
                return ResponseEntity.status(404).body("No districts found for the specified state");
            }
            return ResponseEntity.ok(districts);
        } catch (StateDoesNotExistException e) { // Changed Exception type to StateDoesNotExistException
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }

    @Operation(summary = "Returns all divisions for a given state")
    @GetMapping("/divisions/{stateName}")
    public ResponseEntity<?> getDivisionsForState(@PathVariable String stateName){
        List<String> divisions = pincodeService.getDivisionsByStateName(stateName);
        if(divisions.isEmpty()){
            return ResponseEntity.status(404).body("No divisions found for the specified state");
        }
        return ResponseEntity.ok(divisions);
    }
}
