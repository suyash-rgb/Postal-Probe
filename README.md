# Postal Probe : Phase 3

![Alt Text](https://github.com/suyash-rgb/Postal-Probe/blob/a21b5dfb80881779547609535c90f21c22b30f89/images/Postal%20Probe%20thumbnail.PNG)


**Project Description:**
PostalProbe is a comprehensive solution designed to provide easy access to and management of Indian postal service data.  This project aims to streamline the process of retrieving information about post offices, pin codes, and related details, offering a robust API and tools for developers, businesses, and individuals. It addresses the challenges of accessing and utilizing this data, promoting efficiency and innovation in various sectors.  This project was initially conceived to solve the challenges in a previous project, "Postal Probe Phase 2".

## Table of Contents
* [Development Journey](#development-journey)
* [Features](#features)
* [Architecture](#architecture)
* [Installation](#installation)
* [Usage](#usage)
* [API Documentation](#api-documentation)
* [Data Sources](#data-sources)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Future Enhancements](#future-enhancements)

## Development Journey

### Initial Phase: Pincode Lookup

The journey of Postal Probe began with a project initially named Pincode Lookup. It was created using Google Apps Script and Google Sheets, intended for integration into a larger project. However, due to the substantial size of the database (containing over 1.5 lakh pincode records), the API response times were excessively slow, ranging from 15 to 20 seconds. Over time, additional functionalities were added to enhance the project.

**Checkout the Repository:** [Pincode LookUp (Postal Probe: Phase 1)](https://github.com/suyash-rgb/PinCode-API-Project/tree/main)

**Download the Source Sheet here:** [CSV File](https://github.com/suyash-rgb/PinCode-API-Project/blob/main/Pincode%20Source%20Sheet.csv)

### Transition to Postal Probe

To overcome the limitations of the initial version, I decided to replicate the database and integrate a portion of it statically into the service layer of Postal Probe. The decision to redevelop the API using C# and ASP .NET Core Framework was driven by several factors:

- **Familiarity:** C# is syntactically similar to Java, which is beneficial for me as a Java developer.
- **Exploration:** I wanted to explore the features of Swagger UI.
- **Testing:** I aimed to understand how unit testing is implemented in this environment.

**Checkout the Repository here:** [Postal Probe: Phase 2](https://github.com/suyash-rgb/PinCode-API-Project/tree/asp.net-C%23-API)

### Database Complexity

The database architecture is intricate, consisting of the following nine attributes: CircleName, RegionName, DivisionName, OfficeName, Pincode, OfficeType, Delivery, District, and StateName. No single attribute could uniquely identify a record in the database. After extensive analysis and studying the data for several weeks, I identified that a combination of three attributes was required to uniquely identify a record. However, during development, I discovered instances where this combination still yielded multiple records.

### Composite Primary Key

To address this, I identified a fourth attribute that could be used along with the previous three to uniquely identify a record. Therefore, the composite primary key for the database was established as:  <br>
`COMPOSITE PRIMARY KEY (OfficeName, Pincode, District, DivisionName)`


## Features

* **Comprehensive Data Coverage:** Access to detailed information on post offices, pin codes, districts, states, and more.
* **RESTful API:** A well-documented API for seamless integration with various applications and services.
* **User-Friendly Tools:** Intuitive tools and interfaces for easy data retrieval and management.
* **Search Functionality:** Ability to search postal data based on various parameters.
* **Filters and Pagination:** Functionality to filter and paginate results for efficient data retrieval.

## Architecture

[Describe the system architecture of PostalProbe.  This could include diagrams and explanations of the different components and technologies used.  For example:]

* **Backend:** [Java Spring Boot]
    * API Design: RESTful principles, Transationl endpoints.
    * Business Logic:  Explanation of the core logic and data processing.
* **Database:** [e.g., MySQL/PostgreSQL]
    * Schema Design:  Description of the database tables and relationships.
* **Cloud Platform:** [AWS] 
    * Services used:  [RDS, VPS]


## Installation

1.  **Prerequisites:**
    * Java 17 SDK
    * MySQL Workbench

2.  **Installation Steps:**
    * **Backend:**
        1.  Clone the repository:
            ```bash
            git clone <repository_url>
            cd backend
            ```
        2.  Install dependencies:
            ```bash
            # Example for a Spring Boot project with Maven
            mvn clean install
            ```
        3.  Configure the application:
            * Create a configuration file (`application.properties` for Spring Boot)
            * Set database connection details and other settings.
        4.  Set up the database:
             * Create the database schema.
             * Import data from the provided CSV file. (PS: Its gonna take a while...)
        5.  Run the backend server:
           

## Usage
* **Using the API:**
    * Base URL:  `http://localhost:8080/pincode-api/`
    * Authentication:  Not required
    * Example requests and responses for key endpoints:
        * Get pincode details:
            ```
            GET /api/pincodes/{pincode}
            ```json
            {
              "pincode": "452001",
              "officeName": "GPO Indore",
              "district": "Indore",
              "state": "Madhya Pradesh"
            }
            ```
        * Search for post offices:
             ```
             GET /api/postoffices?search={query}&district={district}&state={state}
             ```json
            [
              {
                "officeName": "GPO Indore",
                "pincode": "452001",
                "district": "Indore",
                "state": "Madhya Pradesh"
              },
              {
                "officeName": "Sneh Nagar",
                "pincode": "452018",
                "district": "Indore",
                "state": "Madhya Pradesh"
              }
            ]
            ```
             
## API Documentation

1. **Display All Pincode Records (Pagination Applied)** <br>
**Method:** GET <br>
**cURL:** `https://localhost:8080/pincode-api/getallpincoderecords?page={{page}}&size={{size}}` <br>
**Description:** <br>
    - Returns a paginated list of all pincode records.<br>
    - The response is a Page object containing Pincode entities.<br>
    - Parameters 'page' and 'size' are used for pagination.<br>
    - 'page' specifies the page number to retrieve (default: 0).<br>
    - 'size' specifies the number of records per page (default: 10).<br>
    - The response includes details like pincode, office name, district, state, etc., for each pincode record.<br>
 <br><br>

2. **Search Pincode Records by Pincode** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/searchbypincode/{{pincode}}` <br>
**Description:** <br>
    - Searches for pincode records by the provided pincode. <br>
    - Returns an error message `No record found for this pincode` with a 404 status if no matching records are found. <br>
    - Returns a single Pincode object if exactly one record matches the pincode.  The object includes details like pincode, office name, district, state, etc. <br>
    - Returns a list of office names if multiple records match the pincode. <br><br>

3. **Search Pincode Records by District Name** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/searchbydistrict/{{district}}` <br>
**Description:** <br>
    - Searches for pincode records by the provided district name. <br>
    - Returns an error message `No record found for this district` with a 404 status if no matching records are found. <br>
    - Returns a list of objects if one or more records match the district name. Each object contains the following fields: <br>
         `officeName`: The name of the office. <br>
         `pincode`: The pincode. <br>
         `delivery`: The delivery details. <br><br>

4. **Returns a list of Offices on entering the District and OfficeType** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/offices/district/{district}/type/{officeType}` <br>
**Description:** <br>
    - Searches for offices by the provided district name and office type. <br>
    - Returns an error message `No offices found for the specified district and office type` with a 404 status if no matching records are found. <br>
    - Returns a list of Pincode objects if one or more records match the district and office type. <br><br>

5. **Returns a list of Offices on entering the Division and OfficeType** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/offices/division/{division}/type/{officeType}` <br>
**Description:** <br>
    - Searches for offices by the provided division name and office type. <br>
    - Returns an error message `No offices found for the specified division and office type` with a 404 status if no matching records are found. <br>
    - Returns a list of Pincode objects if one or more records match the division and office type.<br><br>

6. **Returns the type of Office on entering the name of Office** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/office-type/{officeName}` <br>
**Description:** <br>
    - Searches for the office type by the provided office name. <br>
    - Returns an error message `No office type found for the specified office name` with a 404 status if no matching record is found. <br>
    - Returns the office type as a string if a record matches the office name.<br><br>

7. **Suggest Office Name Matches**
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/search-office-name-starts-with` <br>
**Description:** <br>
This endpoint suggests matching office names based on the provided starting string. It searches the database for office names that begin with the given officeName parameter. <br><br>
**Request Parameters:** <br>
     officeName (String, Query Parameter): The starting string for the office name search. <br><br>
**Response:**
    - If a single match is found, the pincode of that office is returned.
    - If multiple matches are found, a list of Pincode objects is returned.
    - 404 Not Found: Returned if no office names match the provided starting string. <br><br>

8. **Get Districts for a Given State** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/districts/{stateName}` <br>
**Description:** <br>
This endpoint retrieves all districts for a given state name. <br><br>
**Path Parameters:** <br>
     stateName (String): The name of the state for which to retrieve districts.<br><br>
**Response:** <br>
    - 200 OK: Returns a list of strings, where each string represents a district in the specified state.
    - Returns a 404 status with the message "No districts found for the specified state" if no districts are found for the given state.
    - Returns a 404 status with the error message from the StateDoesNotExistException if the state name is invalid. <br><br>

9. **Get Divisions for a Given State** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/divisions/{stateName}` <br>
**Description:** <br>
This endpoint retrieves all divisions for a given state name. <br><br>
**Path Parameters:** <br>
     stateName (String): The name of the state for which to retrieve the divisions.<br><br>
**Response:** <br>
    - 200 OK: Returns a list of strings, where each string represents a division in the specified state.
    - Returns a 404 status with the message "No divisions found for the specified state" if no divisions are found for the given state. <br><br>

10. **Get Delivery Status by Pincode** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/delivery/pincode/{pincode}` <br>
**Description:** <br>
This endpoint returns the delivery status for a given pincode.  It checks if delivery services are available for that pincode. <br><br>
**Path Parameters:** <br>
     pincode (int): The pincode to check the delivery status for.<br><br>
**Response:** <br>
    - 200 OK: Returns a string indicating the delivery status: <br>
             - `Delivery Services Available` if delivery is available for the pincode. <br>
             - `Delivery Services Not Available` for the entered Pincode" if delivery is not available. <br>
    - Returns a 404 status with the message `No record found for this pincode` if the pincode is not found in the system. <br><br>


11. **Get Delivery Status by Office Name** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/delivery/officeName/{officeName}` <br>
**Description:** <br>
This endpoint returns the delivery status for a given office name. It checks if delivery services are available for the pincode(s) associated with that office name. <br><br>
**Path Parameters:** <br>
     officeName (String): The name of the office to check the delivery status for.<br><br>
**Response:** <br>
    - 200 OK: If a single record is found for the office name, returns a string indicating the delivery status: <br>
             - `Delivery Services Available` if delivery is available. <br>
             - `Delivery Services Not Available for the entered Office` if delivery is not available.<br>
    - 200 OK: If multiple records are found for the office name, returns a list of objects. Each object contains: <br>
             - pincode (int): The pincode of the office. <br>
             - officeName (String): The name of the office. <br>
             - delivery (String): The delivery status message ("Delivery Services Available" or "Delivery Services Not Available"). <br>
    - Returns a 404 status with the message `No record found for this Office` if the office name is not found in the system. <br><br>

12. **Get Delivery Status by Primary Key** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/delivery/primary-key` <br>
**Description:** <br>
This endpoint returns the delivery status for a given primary key. <br><br>
**Request Body:** <br>
     PincodePrimaryKey (object): A JSON object representing the primary key.  The structure of this object depends on the definition of the PincodePrimaryKey class.  It contain fields that uniquely identify a pincode record (e.g., pincode, officeName, etc.).
**Response:** <br>
    - 200 OK: Returns a string indicating the delivery status.  The exact string returned depends on the logic within the deliveryService.getDeliveryStatusForPrimaryKey method.<br>
    - Returns a 404 status with the message `No record found for the provided composite key` if no record matches the provided primary key. <br><br>

13. **Update Delivery Status for a Pincode** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/delivery/update-delivery-status` <br>
**Description:** <br>
This endpoint updates the delivery status for a specific pincode, identified by its primary key. <br><br>
**Request Body:** <br>
     PincodePrimaryKey (object): A JSON object representing the primary key of the pincode record to update.  The structure of this object depends on the definition of the PincodePrimaryKey class.<br><br>
**Request Parameters:** <br>
     newDeliveryStatus (String): The new delivery status to set for the pincode.<br><br>
**Response:** <br>
    - 200 OK:  Returns a 200 status with the message "Delivery status updated successfully." if the update is successful.
    - 400 Bad Request: Returns a 400 status with an error message if the delivery status cannot be changed (e.g., invalid status, state not changed).
    - 404 Not Found: Returns a 404 status with an error message if the pincode with the given primary key is not found.<br><br>

14. **Check Delivery Status for a Given Pincode** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/checkDeliveryStatusForPincode/{pincode}` <br>
**Description:** <br>
This endpoint checks the delivery status for a given pincode.  The logic used to determine the delivery status can vary based on the optional algo parameter.<br><br>
**Path Parameters:** <br>
     pincode (int): The pincode to check the delivery status for.<br><br>
**Request Parameters:** <br>
    - algo (String, optional):  A string specifying the algorithm to use for checking the delivery status.  If not provided, a default algorithm is used. Possible values might be "algo1", "algo2", etc., but the specific values are determined by the deliveryService implementation.
    - officeName (String, optional): The office name. <br><br>
**Response:** <br>
The response is determined by the deliveryService.checkDeliveryStatusForPincode() method. It can vary based on the algo parameter and the pincode.  It could be:
    - 200 OK: With a body containing the delivery status or other relevant information.
    - 400 Bad Request: If the pincode is invalid or other input validation fails.
    - 404 Not Found: If no delivery status is found for the given pincode.<br><br>

15. **Stop Delivery for all Pincodes in a Specified Circle** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/stop-delivery/circle/{circleName}` <br>
**Description:** <br>
This endpoint stops delivery (sets the delivery status to 'Non Delivery') for all pincodes within the specified circle..<br><br>
**Path Parameters:** <br>
     circleName (String): The name of the circle for which to stop delivery.<br><br>
**Response:** <br>
    - 200 OK: A JSON object containing a transactionId (UUID) representing the unique identifier for the operation.
    - 404 NOT FOUND: A JSON object containing an error key with a null UUID. This can happen if the circle does not exist or if there's an IllegalArgumentException.
    - 500 Internal Server Error: A JSON object containing an error key with a null UUID, indicating a server error during the operation.<br><br>

16. **Rollback Delivery Status Change for a Circle** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/rollback-delivery/circle/{transactionId}` <br>
**Description:** <br>
This endpoint rolls back a previously made change to the delivery status for all pincodes in a circle.  It uses the transactionId to identify the specific delivery status change to revert.<br><br>
**Path Parameters:** <br>
     transactionId (UUID): The unique ID of the transaction to rollback.  This ID would have been returned by the /stop-delivery/circle/{circleName} endpoint.<br><br>
**Response:** <br>
    - 200 OK: A string message: "Delivery status change rolled back.
    - 404 NOT FOUND: The error message from the TransactionNotFoundException, indicating that the provided transactionId does not correspond to a valid transaction.
    - 500 Internal Server Error: A string containing a generic error message along with the specific exception message, indicating that an unexpected error occurred during the rollback process.<br><br>

17. **Stop Delivery for all Pincodes in a Specified Region** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/stop-delivery/region/{regionName}` <br>
**Description:** <br>
This endpoint stops delivery (sets the delivery status to 'Non Delivery') for all pincodes within the specified region.<br><br>
**Path Parameters:** <br>
     regionName (String): The name of the region for which to stop delivery.<br><br>
**Response:** <br>
    - 200 OK: A JSON object containing a transactionId (UUID) representing the unique identifier for the operation.
    - 404 NOT FOUND: A JSON object containing an error key with a null UUID. This can happen if the region does not exist or if there's an IllegalArgumentException.
    - 500 Internal Server Error: A JSON object containing an error key with a null UUID, indicating a server error during the operation.<br><br>

18. **Rollback Delivery Status Change for a Region** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/rollback-delivery/region/{transactionId}` <br>
**Description:** <br>
This endpoint rolls back a previously made change to the delivery status for all pincodes in a region.  It uses the transactionId to identify the specific delivery status change to revert.<br><br>
**Path Parameters:** <br>
     transactionId (UUID): The unique ID of the transaction to rollback.  This ID would have been returned by the /stop-delivery/region/{regionName} endpoint.<br><br>
**Response:** <br>
    - 200 OK: A string message: "Delivery status change rolled back.
    - 404 NOT FOUND: The error message from the TransactionNotFoundException, indicating that the provided transactionId does not correspond to a valid transaction.
    - 500 Internal Server Error: A string containing a generic error message along with the specific exception message, indicating that an unexpected error occurred during the rollback process.<br><br>

19. **Stop Delivery for all Pincodes in a Specified State** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/stop-delivery/state/{statenName}` <br>
**Description:** <br>
This endpoint stops delivery (sets the delivery status to 'Non Delivery') for all pincodes within the specified state.<br><br>
**Path Parameters:** <br>
     stateName (String): The name of the state for which to stop delivery.<br><br>
**Response:** <br>
    - 200 OK: A JSON object containing a transactionId (UUID) representing the unique identifier for the operation.
    - 404 NOT FOUND: A JSON object containing an error key with a null UUID. This can happen if the state does not exist or if there's an IllegalArgumentException.
    - 500 Internal Server Error: A JSON object containing an error key with a null UUID, indicating a server error during the operation.<br><br>

20. **Rollback Delivery Status Change for a State** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/rollback-delivery/state/{transactionId}` <br>
**Description:** <br>
This endpoint rolls back a previously made change to the delivery status for all pincodes in a state.  It uses the transactionId to identify the specific delivery status change to revert.<br><br>
**Path Parameters:** <br>
     transactionId (UUID): The unique ID of the transaction to rollback.  This ID would have been returned by the /stop-delivery/state/{stateName} endpoint.<br><br>
**Response:** <br>
    - 200 OK: A string message: "Delivery status change rolled back.
    - 404 NOT FOUND: The error message from the TransactionNotFoundException, indicating that the provided transactionId does not correspond to a valid transaction.
    - 500 Internal Server Error: A string containing a generic error message along with the specific exception message, indicating that an unexpected error occurred during the rollback process.<br><br>

21. **Stop Delivery for all Pincodes in a Specified Division** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/stop-delivery/division/{divisionName}` <br>
**Description:** <br>
This endpoint stops delivery (sets the delivery status to 'Non Delivery') for all pincodes within the specified division.<br><br>
**Path Parameters:** <br>
     divisionName (String): The name of the division for which to stop delivery.<br><br>
**Response:** <br>
    - 200 OK: A JSON object containing a transactionId (UUID) representing the unique identifier for the operation.
    - 404 NOT FOUND: A JSON object containing an error key with a null UUID. This can happen if the division does not exist or if there's an IllegalArgumentException.
    - 500 Internal Server Error: A JSON object containing an error key with a null UUID, indicating a server error during the operation.<br><br>

22. **Rollback Delivery Status Change for a Division** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/rollback-delivery/division/{transactionId}` <br>
**Description:** <br>
This endpoint rolls back a previously made change to the delivery status for all pincodes in a division.  It uses the transactionId to identify the specific delivery status change to revert.<br><br>
**Path Parameters:** <br>
     transactionId (UUID): The unique ID of the transaction to rollback.  This ID would have been returned by the /stop-delivery/division/{divisionName} endpoint.<br><br>
**Response:** <br>
    - 200 OK: A string message: "Delivery status change rolled back.
    - 404 NOT FOUND: The error message from the TransactionNotFoundException, indicating that the provided transactionId does not correspond to a valid transaction.
    - 500 Internal Server Error: A string containing a generic error message along with the specific exception message, indicating that an unexpected error occurred during the rollback process.<br><br>

23. **Stop Delivery for all Pincodes in a Specified District** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/stop-delivery/district/{districtName}` <br>
**Description:** <br>
This endpoint stops delivery (sets the delivery status to 'Non Delivery') for all pincodes within the specified district.<br><br>
**Path Parameters:** <br>
     districtName (String): The name of the district for which to stop delivery.<br><br>
**Response:** <br>
    - 200 OK: A JSON object containing a transactionId (UUID) representing the unique identifier for the operation.
    - 404 NOT FOUND: A JSON object containing an error key with a null UUID. This can happen if the district does not exist or if there's an IllegalArgumentException.
    - 500 Internal Server Error: A JSON object containing an error key with a null UUID, indicating a server error during the operation.<br><br>

24. **Rollback Delivery Status Change for a District** <br>
**Method:** PUT <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/rollback-delivery/district/{transactionId}` <br>
**Description:** <br>
This endpoint rolls back a previously made change to the delivery status for all pincodes in a district.  It uses the transactionId to identify the specific delivery status change to revert.<br><br>
**Path Parameters:** <br>
     transactionId (UUID): The unique ID of the transaction to rollback.  This ID would have been returned by the /stop-delivery/district/{districtName} endpoint.<br><br>
**Response:** <br>
    - 200 OK: A string message: "Delivery status change rolled back.
    - 404 NOT FOUND: The error message from the TransactionNotFoundException, indicating that the provided transactionId does not correspond to a valid transaction.
    - 500 Internal Server Error: A string containing a generic error message along with the specific exception message, indicating that an unexpected error occurred during the rollback process.<br><br>

<br><br>



## Data Sources

[Clearly state the sources of the postal data used in this project.  This is important for transparency and to give credit to the data providers.  Example:]

* The primary source of data is the official website of the Indian Postal Service: [https://www.indiapost.gov.in/](https://www.indiapost.gov.in/)
* Additional data may be sourced from [Source 2] and [Source 3].
  

## Contributing

[Explain how others can contribute to the PostalProbe project.  Follow the guidelines in the general README template, and add any specific instructions relevant to your project.]

## License

[Specify the license under which the PostalProbe project is released.  This is crucial for open-source projects.  If you don't know which license to choose, you can refer to [https://choosealicense.com/](https://choosealicense.com/).  Common licenses include MIT, Apache 2.0, and GPL 3.0.  Example:]

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

[Provide ways for people to contact you or the project maintainers.  Example:]

* Project Maintainer: [Suyash Baoney]
* Email: [suyashbaoney58@gmail.com]
* GitHub: [suyash-rgb](https://github.com/your-github-username)
* Project Repository: [Link to the GitHub repository]


## Future Enhancements

[Describe any planned future features or improvements for the PostalProbe project.  This can help attract contributors and show the project's roadmap.  Example:]

* Support for more data attributes.
* Implement regular data updates.
* Add a user-friendly web interface.
* Improve API performance and scalability.
* Implement data analytics and reporting features.
* Add support for other data formats.

 
