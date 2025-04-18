package com.example.PostalProbe.service;

import com.example.PostalProbe.DTOs.PincodeUpdateRequest;
import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.*;
import com.example.PostalProbe.repository.PincodeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryService {

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Using ConcurrentHashMap for thread safety
    private final Map<UUID, List<Pincode>> transactionStateData = new ConcurrentHashMap<>();

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

    public List<Pincode> findByRegionName(String regionName) {
        return pincodeRepository.findByRegionName(regionName);
    }

    @Transactional
    public UUID stopDeliveryForRegion(String regionName) {
        if(!pincodeRepository.existsByRegionName(regionName)){
            throw new RegionDoesNotExistException("Region "+regionName+" does not exist in the database.");
        }

        UUID transactionId = UUID.randomUUID();
        try {
            List<Pincode> originalPincodes = pincodeRepository.findByRegionName(regionName);
            transactionStateData.put(transactionId, originalPincodes);
            jdbcTemplate.update("CALL StopDeliveryForRegion(?)", regionName);
            return transactionId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
            throw e;
        }
    }

    @Transactional
    public void rollbackStopDeliveryForRegion(UUID transactionId) {
        if (!transactionStateData.containsKey(transactionId)) {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        try {
            List<Pincode> originalPincodes = transactionStateData.get(transactionId);
            for (Pincode pincode : originalPincodes) {
                jdbcTemplate.update(
                        "UPDATE pincode SET delivery = ? WHERE office_name = ? AND pincode = ? AND district = ? AND division_name = ?",
                        pincode.getDelivery(),
                        pincode.getPincodePrimaryKey().getOfficeName(),
                        pincode.getPincodePrimaryKey().getPincode(),
                        pincode.getPincodePrimaryKey().getDistrict(),
                        pincode.getPincodePrimaryKey().getDivisionName()
                );
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional
    public void startDeliveryForRegion(String regionName){
        if(!pincodeRepository.existsByRegionName(regionName)){
            throw new RegionDoesNotExistException("Region "+regionName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StartDeliveryForRegion(?)", regionName);
    }

    //Added a new method to get pincodes by state name
    public List<Pincode> findByStateName(String stateName) {
        return pincodeRepository.findByStateName(stateName);
    }

    @Transactional
    public UUID stopDeliveryForState(String stateName) {
        if (!pincodeRepository.existsByStateName(stateName)) {
            throw new StateDoesNotExistException("State " + stateName + " does not exist in the database.");
        }
        UUID transactionId = UUID.randomUUID();
        try {
            // 1. Fetch the Pincode data *before* the update and store it in-memory
            List<Pincode> originalPincodes = pincodeRepository.findByStateName(stateName);
            transactionStateData.put(transactionId, originalPincodes); // Store original data

            // 2. Execute the stored procedure to stop delivery
            jdbcTemplate.update("CALL StopDeliveryForState(?)", stateName);

            return transactionId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId); // Remove on error
            throw e;
        }
    }

    @Transactional
    public void rollbackStopDeliveryForState(UUID transactionId) {
        if (!transactionStateData.containsKey(transactionId)) {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        try {
            List<Pincode> originalPincodes = transactionStateData.get(transactionId);

            // 3. Restore the original data (persist from in-memory to db)
            for (Pincode pincode : originalPincodes) {
                jdbcTemplate.update(
                        "UPDATE pincode SET delivery = ? WHERE office_name = ? AND pincode = ? AND district = ? AND division_name = ?",
                        pincode.getDelivery(),
                        pincode.getPincodePrimaryKey().getOfficeName(),
                        pincode.getPincodePrimaryKey().getPincode(),
                        pincode.getPincodePrimaryKey().getDistrict(),
                        pincode.getPincodePrimaryKey().getDivisionName()
                );
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // Rollback
            transactionStateData.remove(transactionId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional
    public void startDeliveryForState(String stateName){
        if(!pincodeRepository.existsByStateName(stateName)){
            throw new StateDoesNotExistException("State "+stateName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StartDeliveryForState(?)", stateName);
    }


    public List<Pincode> findByDivisionName(String divisionName) { //potential bug
        return pincodeRepository.findByPincodePrimaryKeyDivisionName(divisionName);
    }

    @Transactional
    public UUID stopDeliveryForDivision(String divisionName) {
        if(!pincodeRepository.existsByPincodePrimaryKeyDivisionName(divisionName)){
            throw new DivisionDoesNotExistException("Division "+divisionName+" does not exist in the database.");
        }
        if (pincodeRepository.countStatesByDivisionName(divisionName) > 1) {
            throw new MultipleOccurancesException("Division '" + divisionName + "' found in multiple states.");
        }
        UUID transactionId = UUID.randomUUID();
        try {
            List<Pincode> originalPincodes = pincodeRepository.findByPincodePrimaryKeyDivisionName(divisionName);
            transactionStateData.put(transactionId, originalPincodes);
            jdbcTemplate.update("CALL StopDeliveryForDivision(?)", divisionName);
            return transactionId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
            throw e;
        }
    }

    @Transactional
    public void rollbackStopDeliveryForDivision(UUID transactionId) {
        if (!transactionStateData.containsKey(transactionId)) {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        try {
            List<Pincode> originalPincodes = transactionStateData.get(transactionId);
            for (Pincode pincode : originalPincodes) {
                jdbcTemplate.update(
                        "UPDATE pincode SET delivery = ? WHERE office_name = ? AND pincode = ? AND district = ? AND division_name = ?",
                        pincode.getDelivery(),
                        pincode.getPincodePrimaryKey().getOfficeName(),
                        pincode.getPincodePrimaryKey().getPincode(),
                        pincode.getPincodePrimaryKey().getDistrict(),
                        pincode.getPincodePrimaryKey().getDivisionName()
                );
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
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

    public List<Pincode> findByDistrictName(String districtName) {
        return pincodeRepository.findByPincodePrimaryKeyDistrict(districtName);
    }

    @Transactional
    public UUID stopDeliveryForDistrict(String district) {
        if (!pincodeRepository.existsByPincodePrimaryKeyDistrict(district)) {
            throw new DistrictDoesNotExistException("District '" + district + "' does not exist in the database.");
        }
        // Check if the district exists in multiple states/divisions of same state
        if (pincodeRepository.countByPincodePrimaryKeyDistrict(district) > 1) {
            throw new MultipleOccurancesException("District '" + district + "' found in multiple states or divisions");
        }
        UUID transactionId = UUID.randomUUID();
        try {
            List<Pincode> originalPincodes = pincodeRepository.findByPincodePrimaryKeyDistrict(district);
            transactionStateData.put(transactionId, originalPincodes);
            jdbcTemplate.update("CALL StopDeliveryForDistrict(?)", district);
            return transactionId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
            throw e;
        }
    }

    @Transactional
    public void rollbackStopDeliveryForDistrict(UUID transactionId) {
        if (!transactionStateData.containsKey(transactionId)) {
            throw new TransactionNotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        try {
            List<Pincode> originalPincodes = transactionStateData.get(transactionId);
            for (Pincode pincode : originalPincodes) {
                jdbcTemplate.update(
                        "UPDATE pincode SET delivery = ? WHERE office_name = ? AND pincode = ? AND district = ? AND division_name = ?",
                        pincode.getDelivery(),
                        pincode.getPincodePrimaryKey().getOfficeName(),
                        pincode.getPincodePrimaryKey().getPincode(),
                        pincode.getPincodePrimaryKey().getDistrict(),
                        pincode.getPincodePrimaryKey().getDivisionName()
                );
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
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
