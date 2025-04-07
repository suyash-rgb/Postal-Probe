package com.example.postalprobe.service;

import com.example.postalprobe.entity.PincodeRecords;
import com.example.postalprobe.repository.PincodeRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PincodeRecordsService {

    @Autowired
    private PincodeRecordsRepository pincodeRecordsRepository;


    public List<PincodeRecords> findByOfficeName(String officeName){
        return pincodeRecordsRepository.findByOfficeName(officeName);
    }

}
