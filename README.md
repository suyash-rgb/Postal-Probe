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
* **Data Accuracy and Updates:** Regular updates to ensure data accuracy and reflect the latest changes in the postal service.
* **Scalability and Performance:** Designed to handle a large volume of requests and provide fast response times.
* **User-Friendly Tools:** Intuitive tools and interfaces for easy data retrieval and management.
* **Secure Authentication:** Robust authentication and authorization mechanisms to protect data access.
* **Search Functionality:** Ability to search postal data based on various parameters.
* **Filters and Pagination:** Functionality to filter and paginate results for efficient data retrieval.
* **Bulk Data Export:** Options to export data in various formats (e.g., CSV, JSON) for offline use.

## Architecture

[Describe the system architecture of PostalProbe.  This could include diagrams and explanations of the different components and technologies used.  For example:]

* **Backend:** [e.g., Java Spring Boot, Python Django, Node.js Express]
    * API Design: RESTful principles, endpoints, request/response formats.
    * Business Logic:  Explanation of the core logic and data processing.
* **Database:** [e.g., MySQL, PostgreSQL, MongoDB]
    * Schema Design:  Description of the database tables and relationships.
* **Cloud Platform:** [e.g., AWS, Azure, Google Cloud] (if applicable)
    * Services used:  [e.g., EC2, RDS, S3, Lambda]
* **DevOps:**
    * CI/CD Pipeline: [e.g., GitHub Actions, Jenkins]
    * Deployment Strategy: [e.g., Docker, Kubernetes]
* **Diagram:** (You can include a simple architecture diagram here using Markdown.  For more complex diagrams, you might want to link to an image file.)

## Installation

[Provide detailed instructions on how to install and set up the PostalProbe project.  This should be clear and easy to follow for developers who want to use or contribute to the project.  Example:]

1.  **Prerequisites:**
    * [List any software dependencies, e.g., Java 11+, Python 3.8+, Node.js 14+, Docker, etc.]
    * [Database server (e.g., MySQL, PostgreSQL) and credentials]
    * [Any API keys or environment variables required]

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
            * Create a configuration file (e.g., `application.properties` for Spring Boot, `.env` for Python/Node.js)
            * Set database connection details, API keys, and other settings.
        4.  Set up the database:
             * Create the database schema.
             * Run migrations (if applicable).
        5.  Run the backend server:
            ```bash
            # Example for Spring Boot
            java -jar target/postalprobe.jar
            ```

## Usage

[Explain how to use the PostalProbe project.  Provide examples of how to access the API, use the tools, and retrieve data.  Include code snippets and sample requests/responses where appropriate.   розділ Usage може включати наступні підрозділи:]

* **Using the API:**
    * Base URL:  `[Your API Base URL]`
    * Authentication:  [Describe authentication methods, e.g., API keys, OAuth 2.0]
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

[Provide comprehensive documentation for the PostalProbe API.  This should include:]

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
    - Returns an error message "No offices found for the specified district and office type" with a 404 status if no matching records are found. <br>
    - Returns a list of Pincode objects if one or more records match the district and office type. <br><br>

5. **Returns a list of Offices on entering the Division and OfficeType** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/offices/division/{division}/type/{officeType}` <br>
**Description:** <br>
    - Searches for offices by the provided division name and office type. <br>
    - Returns an error message "No offices found for the specified division and office type" with a 404 status if no matching records are found. <br>
    - Returns a list of Pincode objects if one or more records match the division and office type.<br><br>

6. **Returns the type of Office on entering the name of Office** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/office-type/{officeName}` <br>
**Description:** <br>
    - Searches for the office type by the provided office name. <br>
    - Returns an error message "No office type found for the specified office name" with a 404 status if no matching record is found. <br>
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
    - Returns a 404 status with the message "No divisions found for the specified state" if no divisions are found for the given state.

10. **Get Delivery Status by Pincode** <br>
**Method:** GET <br>
**cURL:** `http://localhost:8080/pincode-api/delivery_controller/delivery/pincode/{pincode}` <br>
**Description:** <br>
This endpoint returns the delivery status for a given pincode.  It checks if delivery services are available for that pincode. <br><br>
**Path Parameters:** <br>
     pincode (int): The pincode to check the delivery status for.<br><br>
**Response:** <br>
    - 200 OK: Returns a string indicating the delivery status: <br>
             - "Delivery Services Available" if delivery is available for the pincode. <br>
             - "Delivery Services Not Available for the entered Pincode" if delivery is not available. <br>
    - Returns a 404 status with the message "No record found for this pincode" if the pincode is not found in the system. <br><br>


<br><br>
* Consider including a link to the Swagger UI here.<br>

## Data Sources

[Clearly state the sources of the postal data used in this project.  This is important for transparency and to give credit to the data providers.  Example:]

* The primary source of data is the official website of the Indian Postal Service: [https://www.indiapost.gov.in/](https://www.indiapost.gov.in/)
* Additional data may be sourced from [Source 2] and [Source 3].
  

## Contributing

[Explain how others can contribute to the PostalProbe project.  Follow the guidelines in the general README template, and add any specific instructions relevant to your project.  Link to CONTRIBUTING.md if it is a long process]

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

 
