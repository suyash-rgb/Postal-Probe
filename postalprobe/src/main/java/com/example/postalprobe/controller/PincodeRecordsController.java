package com.example.postalprobe.controller;

import com.example.postalprobe.entity.PincodeRecords;
import com.example.postalprobe.service.PincodeRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/postal-probe-api")
public class PincodeRecordsController {

    @Autowired
    private PincodeRecordsService pincodeRecordsService;


    @GetMapping("/searchbyofficename/{officeName}")
    public ResponseEntity<?> searchByOfficeName(@PathVariable String officeName){
        List<PincodeRecords> pincodeRecords = pincodeRecordsService.findByOfficeName(officeName);
        if(pincodeRecords.isEmpty()){
            return ResponseEntity.status(404).body("No results for this office name");
        } else if (pincodeRecords.size()==1) {
            PincodeRecords pincodeRecord = pincodeRecords.get(0);
            return ResponseEntity.ok(new Object(){
                public int pincodeValue = pincodeRecord.getPincode();
                public boolean deliveryStatus = pincodeRecord.getDelivery();
            });
        } else {
            return ResponseEntity.ok(pincodeRecords.stream()
                    .map(p -> new Object(){
                        public String circleName = p.getCircleName();
                        public String divisionName = p.getDivisionName();
                        public int pincode = p.getPincode();
                        public String district = p.getDistrict();
                        public String stateName = p.getStateName();
                    }).toList());
        }
    }



}
