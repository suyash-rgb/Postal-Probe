package com.example.PostalProbe.service;

import com.example.PostalProbe.DTOs.CheckDeliveryStatusResponse;

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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Using ConcurrentHashMap for thread safety
    private final Map<UUID, List<Pincode>> transactionStateData = new ConcurrentHashMap<>();


    //for updating only the delivery status
    @Transactional
    public void updateDeliveryStatus(PincodePrimaryKey primaryKey, String newDeliveryStatus) {
        Pincode pincode = pincodeRepository.findById(primaryKey)
                .orElseThrow(() -> new PincodeNotFoundException("Pincode not found with primary key: " + primaryKey));

        String existingDeliveryStatus = pincode.getDelivery();

        if ("Non Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Delivery".equalsIgnoreCase(newDeliveryStatus)) {
            pincode.setDelivery("Delivery");
            pincodeRepository.save(pincode); // Save the updated entity
        } else if ("Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Non Delivery".equalsIgnoreCase(newDeliveryStatus)) {
            throw new CannotChangeDeliveryStatusException("Cannot change delivery status from Delivery to Non Delivery");
        } else {
            throw new StateNotChangedException("Delivery was not changed in the request. Please check the current delivery status again.");
        }
    }

    public void updateDeliveryStatus(Pincode existingPincode, PincodeUpdateRequest updateRequest) {
        if (updateRequest.getDelivery() != null) {

            String existingDeliveryStatus = existingPincode.getDelivery();  // Get the existing String value
            String requestedDeliveryStatus = updateRequest.getDelivery(); // Get the requested String value

            if ("Non Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Delivery".equalsIgnoreCase(requestedDeliveryStatus)) {
                existingPincode.setDelivery("Delivery"); // Set the String value
            } else if ("Delivery".equalsIgnoreCase(existingDeliveryStatus) && "Non Delivery".equalsIgnoreCase(requestedDeliveryStatus)) {
                throw new CannotChangeDeliveryStatusException("Cannot change delivery status from Delivery to Non Delivery");
            } else {
                throw new StateNotChangedException("Delivery was not changed in the request. Please check the current delivery status again.");
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

    public ResponseEntity<?> checkDeliveryStatusForPincode(int pincode, String algo, String officeName) {
        List<Pincode> pincodes = pincodeRepository.findByPincodePrimaryKeyPincode(pincode);

        if (pincodes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No record found for this pincode");
        }

        if (pincodes.size() == 1) {
            String officeNameResult = removeOfficeSuffix(pincodes.get(0).getOfficeName());
            String deliveryStatus = pincodes.get(0).getDelivery();
            CheckDeliveryStatusResponse response = new CheckDeliveryStatusResponse(officeNameResult, deliveryStatus);
            return ResponseEntity.ok(response);
        }

        if ("algo1".equalsIgnoreCase(algo)) {
            String officeNameResult = removeOfficeSuffix(pincodes.get(0).getOfficeName());
            String deliveryStatus = pincodes.get(0).getDelivery();
            CheckDeliveryStatusResponse response = new CheckDeliveryStatusResponse(officeNameResult, deliveryStatus);
            return ResponseEntity.ok(response);
        }

        if ("algo2".equalsIgnoreCase(algo)) {
            boolean allDelivery = pincodes.stream()
                    .allMatch(p -> "Delivery".equalsIgnoreCase(p.getDelivery()));
            String finalDeliveryStatus = allDelivery ? "Delivery" : "Non Delivery";
            return ResponseEntity.ok(finalDeliveryStatus);
        }

        if ("algo3".equalsIgnoreCase(algo)) {
            boolean allDelivery = pincodes.stream()
                    .anyMatch(p -> "Delivery".equalsIgnoreCase(p.getDelivery()));
            String finalDeliveryStatus = allDelivery ? "Delivery" : "Non Delivery";
            return ResponseEntity.ok(finalDeliveryStatus);
        }

        if ("algo4".equalsIgnoreCase(algo)) {
            if (officeName != null && !officeName.isEmpty()) {
                List<Pincode> filteredPincodes = pincodes.stream()
                        .filter(p -> p.getOfficeName().equalsIgnoreCase(officeName))
                        .collect(Collectors.toList());
                if (filteredPincodes.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No record found for given office name");
                }
                String officeNameResult = removeOfficeSuffix(filteredPincodes.get(0).getOfficeName());
                String deliveryStatus = filteredPincodes.get(0).getDelivery();
                CheckDeliveryStatusResponse response = new CheckDeliveryStatusResponse(officeNameResult, deliveryStatus);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Please specify the office name for this pincode");
        }

        // Default behavior if no algo is provided, or an invalid algo is provided.
        return ResponseEntity.badRequest().body("Invalid algo parameter. Please specify algo1, algo2 or algo3");
    }

    // Helper method to remove office suffix
    private String removeOfficeSuffix(String officeName) {
        return officeName.replaceAll("(B\\.O|S\\.O|H\\.O)$", "").trim();
    }

    //for circle
    @Transactional
    public UUID stopDeliveryForCircle(String circleName) {
        if(!pincodeRepository.existsByCircleName(circleName)){
            throw new CircleDoesNotExistException("Circle "+circleName+" does not exist in the database.");
        }

        UUID transactionId = UUID.randomUUID();
        try {
            List<Pincode> originalPincodes = pincodeRepository.findByCircleName(circleName);
            transactionStateData.put(transactionId, originalPincodes);
            jdbcTemplate.update("CALL StopDeliveryForCircle(?)", circleName);
            return transactionId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transactionStateData.remove(transactionId);
            throw e;
        }
    }

    @Transactional
    public void rollbackStopDeliveryForCirle(UUID transactionId) {
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
    public void startDeliveryForCircle(String circleName){
        if(!pincodeRepository.existsByRegionName(circleName)){
            throw new RegionDoesNotExistException("Circle "+circleName+" does not exist in the database.");
        }
        jdbcTemplate.update("CALL StartDeliveryForCircle(?)", circleName);
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
                        "UPDATE pincode SET Delivery = ? WHERE OfficeName = ? AND Pincode = ? AND District = ? AND DivisionName = ?",                        pincode.getDelivery(),
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
