package com.example.postalprobe.repository;

import com.example.postalprobe.entity.PincodePrimaryKey;
import com.example.postalprobe.entity.PincodeRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PincodeRecordsRepository extends JpaRepository<PincodeRecords, PincodePrimaryKey> {

    List<PincodeRecords> findByDistrict(String district);

    List<PincodeRecords> findByOfficeName(String officeName);

    Optional<PincodeRecords> getPincodeRecordsByPincodePrimaryKey(PincodePrimaryKey primary);

    List<PincodeRecords> getPincodeRecordsByDistrict(String district);

    List<PincodeRecords> getPincodeRecordsByDistrictAndOfficeType(String district, String officeType);

}
