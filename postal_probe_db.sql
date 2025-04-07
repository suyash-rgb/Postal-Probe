CREATE DATABASE PostalProbe;

USE PostalProbe;

CREATE TABLE `pincode_records` (
  `CircleName` varchar(100) DEFAULT NULL,
  `RegionName` varchar(100) NOT NULL,
  `DivisionName` varchar(100) NOT NULL,
  `OfficeName` varchar(100) NOT NULL,
  `Pincode` int NOT NULL,
  `OfficeType` varchar(2) DEFAULT NULL,
  `Delivery` int DEFAULT NULL,
  `District` varchar(255) NOT NULL,
  `StateName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`OfficeName`,`Pincode`,`District`,`DivisionName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Replicate data from existing db
INSERT INTO PostalProbe.pincode_records
SELECT *
FROM pincodedb.pincode;

SELECT * FROM pincode_records;

SELECT COUNT(*) FROM pincode_records;

SELECT DISTINCT CircleName FROM pincode_records;
SELECT DISTINCT District FROM pincode_records;



