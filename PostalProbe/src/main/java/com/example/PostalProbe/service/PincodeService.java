package com.example.PostalProbe.service;

import com.example.PostalProbe.DTOs.PincodeUpdateRequest;
import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.CannotChangeDeliveryStatusException;
import com.example.PostalProbe.exceptions.PincodeNotFoundException;
import com.example.PostalProbe.exceptions.StateDoesNotExistException;
import com.example.PostalProbe.repository.PincodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PincodeService {

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private DeliveryService deliveryService; // Injecting DeliveryService

    public Page<Pincode> getAllPincodes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // No sorting applied
        return pincodeRepository.findAll(pageable);
    }

    public List<Pincode> searchByPincode(int pincode) {
        return pincodeRepository.findByPincodePrimaryKeyPincode(pincode);
    }

    public List<Pincode> searchByDistrict(String district) {
        return pincodeRepository.findByPincodePrimaryKeyDistrict(district);
    }

    public List<Pincode> searchByOfficeName(String officeName) {
        return pincodeRepository.findByPincodePrimaryKeyOfficeName(officeName);
    }

    public Optional<Pincode> getPincodeByPrimaryKey(PincodePrimaryKey primaryKey) {
        return pincodeRepository.findById(primaryKey);
    }

    public List<Pincode> getOfficesByDistrict(String district, String officeType) {
        return pincodeRepository.findByPincodePrimaryKey_DistrictAndOfficeType(district, officeType);
    }

    public List<Pincode> getOfficesByDivision(String division, String officeType) {
        return pincodeRepository.findByPincodePrimaryKey_DivisionNameAndOfficeType(division, officeType);
    }

    public Optional<String> getOfficeTypeByOfficeName(String officeName) {
        List<String> officeTypes = pincodeRepository.findOfficeTypeByOfficeName(officeName);
        if (officeTypes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(officeTypes.get(0));
    }

    public Pincode updatePincode(PincodePrimaryKey primaryKey, PincodeUpdateRequest updateRequest) {
        Pincode existingPincode = getPincodeById(primaryKey);
        updatePincodeFields(existingPincode, updateRequest);
        deliveryService.updateDeliveryStatus(existingPincode, updateRequest);
        return pincodeRepository.save(existingPincode);
    }

    private Pincode getPincodeById(PincodePrimaryKey primaryKey) {
        return pincodeRepository.findById(primaryKey)
                .orElseThrow(() -> new PincodeNotFoundException("Pincode not found with the given primary key"));
    }

    private void updatePincodeFields(Pincode existingPincode, PincodeUpdateRequest updateRequest) {
        if (updateRequest.getCircleName() != null) {
            existingPincode.setCircleName(updateRequest.getCircleName());
        }
        if (updateRequest.getRegionName() != null) {
            existingPincode.setRegionName(updateRequest.getRegionName());
        }
        if (updateRequest.getOfficeType() != null) {
            existingPincode.setOfficeType(updateRequest.getOfficeType());
        }
        if (updateRequest.getStateName() != null) {
            existingPincode.setStateName(updateRequest.getStateName());
        }
    }


    public Boolean doesStateExist(String stateName){
        return pincodeRepository.existsByStateName(stateName);
    }

    public List<String> getDistrictsByStateName(String stateName){
        if(stateName!=null){
            Boolean check = doesStateExist(stateName);
            if(check){
                return pincodeRepository.findDistrictsByStateName(stateName);
            }
            throw new StateDoesNotExistException("The state entered doesn't exist. Please enter a valid state name.");
        }
        return Collections.emptyList();
    }

    public List<String> getDivisionsByStateName(String stateName){
        return pincodeRepository.findDivisionsByStateName(stateName);
    }



}
