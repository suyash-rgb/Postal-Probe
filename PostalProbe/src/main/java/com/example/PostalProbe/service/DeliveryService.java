package com.example.PostalProbe.service;

import com.example.PostalProbe.DTOs.PincodeUpdateRequest;
import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.*;
import com.example.PostalProbe.repository.PincodeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateDeliveryStatus(Pincode existingPincode, PincodeUpdateRequest updateRequest) {
        if (updateRequest.getDelivery() != null) {

            String existingDeliveryStatus = existingPincode.getDelivery();  // Get the existing String value
            String requestedDeliveryStatus = updateRequest.getDelivery(); // Get the requested String value

            if ("Non Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Delivery".equalsIgnoreCase(requestedDeliveryStatus)) {
                existingPincode.setDelivery("Delivery"); // Set the String value
            } else if ("Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Non Delivery".equalsIgnoreCase(requestedDeliveryStatus)) {
                throw new CannotChangeDeliveryStatusException("Cannot change delivery status from Delivery to Non Delivery");
            }
        }
    }

    public String getDeliveryStatusByPincode(int pincode) {
        List<Pincode> pincodes = pincodeRepository.findByPincodePrimaryKeyPincode(pincode);
        if (pincodes.isEmpty()) {
            return null;
        }
        return pincodes.get(0).getDelivery();
    }

    public List<Pincode> getDeliveryStatusByOfficeName(String officeName) {
        return pincodeRepository.findByPincodePrimaryKeyOfficeName(officeName);
    }

    public String getDeliveryStatusForPrimaryKey(PincodePrimaryKey primaryKey) {
        Optional<Pincode> pincode = pincodeRepository.findById(primaryKey);
        if (pincode.isPresent()) {
            return pincode.get().getDelivery();
        }
        return null; // Or throw an exception
    }

    @Transactional
    public void stopDeliveryForRegion(String regionName){
        if(!pincodeRepository.existsByRegionName(regionName)){
            throw new RegionDoesNotExistException("Region "+regionName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StopDeliveryForRegion(?)", regionName);
    }

    @Transactional
    public void startDeliveryForRegion(String regionName){
        if(!pincodeRepository.existsByRegionName(regionName)){
            throw new RegionDoesNotExistException("Region "+regionName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StartDeliveryForRegion(?)", regionName);
    }

    @Transactional
    public void stopDeliveryForState(String stateName){
        if(!pincodeRepository.existsByStateName(stateName)){
            throw new StateDoesNotExistException("State "+stateName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StopDeliveryForState(?)", stateName);
    }

    @Transactional
    public void startDeliveryForState(String stateName){
        if(!pincodeRepository.existsByStateName(stateName)){
            throw new StateDoesNotExistException("State "+stateName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StartDeliveryForState(?)", stateName);
    }

    @Transactional
    public void stopDeliveryForDivision(String divisionName){
        if(!pincodeRepository.existsByPincodePrimaryKeyDivisionName(divisionName)){
            throw new DivisionDoesNotExistException("Division "+divisionName+" does not exist in the database.");
        }
        if (pincodeRepository.countStatesByDivisionName(divisionName) > 1) {
            throw new MultipleOccurancesException("Division '" + divisionName + "' found in multiple states.");
        }
        jdbcTemplate.update("CALL StopDeliveryForDivison(?)", divisionName);
    }

    @Transactional
    public void startDeliveryForDivision(String divisionName){
        if(!pincodeRepository.existsByPincodePrimaryKeyDivisionName(divisionName)){
            throw new DivisionDoesNotExistException("Division "+divisionName+" does not exist in the database.");
        }
        if (pincodeRepository.countStatesByDivisionName(divisionName) > 1) {
            throw new MultipleOccurancesException("Division '" + divisionName + "' found in multiple states.");
        }
        jdbcTemplate.update("CALL StartDeliveryForDivison(?)", divisionName);
    }

    @Transactional
    public void stopDeliveryForDistrict(String district) {
        if (!pincodeRepository.existsByPincodePrimaryKeyDistrict(district)) {
            throw new DistrictDoesNotExistException("District '" + district + "' does not exist in the database.");
        }
        // Check if the district exists in multiple states/divisions of same state
        if (pincodeRepository.countByPincodePrimaryKeyDistrict(district) > 1) {
            throw new MultipleOccurancesException("District '" + district + "' found in multiple states or divisions");
        }
        jdbcTemplate.update("CALL StopDeliveryForDistrict(?)", district);
    }

    @Transactional
    public void startDeliveryForDistrict(String district) {
        if(!pincodeRepository.existsByPincodePrimaryKeyDistrict(district)){
            throw new DistrictDoesNotExistException("District '" + district + "' does not exist in the database.");
        }
        // Check if the district exists in multiple states/divisions of same state
        if (pincodeRepository.countByPincodePrimaryKeyDistrict(district) > 1) {
            throw new MultipleOccurancesException("District '" + district + "' found in multiple states or divisions");
        }
        jdbcTemplate.update("CALL StartDeliveryForDistrict(?)", district);
    }
}
