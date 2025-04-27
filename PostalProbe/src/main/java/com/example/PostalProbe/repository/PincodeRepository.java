package com.example.PostalProbe.repository;

import com.example.PostalProbe.entity.Pincode;
import com.example.PostalProbe.entity.PincodePrimaryKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, PincodePrimaryKey> {

    Page<Pincode> findAll(Pageable pageable);
    List<Pincode> findByPincodePrimaryKeyPincode(int pincode);

    List<Pincode> findByPincodePrimaryKeyOfficeName(String officeName);

    List<Pincode> findByPincodePrimaryKey_DistrictAndOfficeType(String district, String officeType);

    List<Pincode> findByPincodePrimaryKey_DivisionNameAndOfficeType(String division, String officeType);

    @Query("SELECT p.officeType FROM Pincode p WHERE p.pincodePrimaryKey.officeName LIKE :officeName%")
    List<String> findOfficeTypeByOfficeName(@Param("officeName") String officeName);

    @Query("SELECT p FROM Pincode p WHERE LOWER(p.pincodePrimaryKey.officeName) LIKE LOWER(concat(:officeName, '%'))")
    List<Pincode> findByOfficeNameStartingWith(@Param("officeName") String officeName);

    @Query("SELECT DISTINCT p.pincodePrimaryKey.district FROM Pincode p WHERE p.stateName = :stateName")
    List<String> findDistrictsByStateName(@Param("stateName") String stateName );

    @Query("SELECT DISTINCT p.pincodePrimaryKey.divisionName FROM Pincode p WHERE p.stateName = :stateName")
    List<String> findDivisionsByStateName(@Param("stateName") String stateName);


    //For validation and exception handling
    Boolean existsByStateName(String stateName);

    long countByPincodePrimaryKeyDistrict(String district);

    //boolean existsByPincodePrimaryKeyDivisionName(String divisionName);

    long countByPincodePrimaryKeyDivisionName(String divisionName);

    @Query("SELECT count(distinct p.stateName) FROM Pincode p WHERE p.pincodePrimaryKey.district = :district")
    long countStatesByDistrict(@Param("district") String district);

    @Query("SELECT count(distinct p.stateName) FROM Pincode p WHERE p.pincodePrimaryKey.divisionName = :divisionName")
    long countStatesByDivisionName(@Param("divisionName") String divisionName);

    //List<Pincode> findByPincodePrimaryKeyStateName(String stateName); bug fixed


    List<Pincode> findByRegionName(String regionName);
    Boolean existsByRegionName(String regionName);
    List<Pincode> findByPincodePrimaryKeyDivisionName(String divisionName);
    Boolean existsByPincodePrimaryKeyDivisionName(String divisionName);
    List<Pincode> findByPincodePrimaryKeyDistrict(String district);
    Boolean existsByPincodePrimaryKeyDistrict(String district);

    @Query("SELECT p FROM Pincode p WHERE p.stateName = :stateName") // Corrected query
    List<Pincode> findByStateName(@Param("stateName") String stateName);

    boolean existsByCircleName(String circleName);

    List<Pincode> findByCircleName(String circleName);
}
