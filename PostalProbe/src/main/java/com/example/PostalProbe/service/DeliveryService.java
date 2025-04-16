package com.example.PostalProbe.service;

import com.example.PostalProbe.DTOs.PincodeUpdateRequest;
import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import com.example.PostalProbe.exceptions.CannotChangeDeliveryStatusException;
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
            return null; // Or throw an exception, depending on your error handling policy
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
    public void stopDeliveryForDistrict(String district) {
        jdbcTemplate.update("CALL StopDeliveryForDistrict(?)", district);
    }

    @Transactional
    public void startDeliveryForDistrict(String district) {
        jdbcTemplate.update("CALL StartDeliveryForDistrict(?)", district);
    }
}
