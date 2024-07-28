# Action Factory

## Project Context
Action Factory is being developed by a dedicated team of four as part of our backend bootcamp at BeTek. Our goal is to revolutionize the device validation process for allied providers by creating a robust and automated system. This project not only serves as a practical application of our learning but also aims to deliver a real-world solution to streamline and enhance operational efficiency.

## Project Description
Action Factory seeks to revolutionize the validation process for devices from allied providers. This automated and robust system will efficiently and accurately manage the information of business partners and devices, freeing employees from repetitive manual tasks.

### Company Management:
- **ID**: Provider ID
- **Name**: Provider Name
- **Address**: Provider Address
- **Phone Number**: Provider Phone Number
- **Email**: Provider Email
- **Website**: Provider Website
- **Industry Sector**: Provider Industry Sector
- **Registration Date**: Provider Registration Date

### Functionalities:
1. **Creation and Update of Providers**: Only employees with the coordinator role can perform this action.
2. **Viewing and Searching Providers**.

### Employee Management:
The system will also manage the employees of Action Factory, where employees can be created and assigned specific roles that allow them to perform tasks effectively and responsibly.

#### Roles:
- **Coordinator**: Has the authority to add, update, and validate companies, and to execute and supervise device validations.
- **Validator**: An expert in device validation, executing validations with precision and thoroughness.

#### Employee Attributes:
- **Employee ID**: A unique identifier for each employee in the system.
- **Name**: The full name of the employee.
- **Email**: The employee's email address for communication and identification.
- **Password**: The employee's password for system authentication.
- **Role**: The role assigned to the employee (Coordinator or Validator).
- **Hiring Date**: The date the employee joined Action Factory.
- **Last Access**: The date and time of the employee's last system access.
- **Status**: The current status of the employee in the company (active, inactive, etc.).

### Validation Management:
The core of our company is facilitating the validation and management of devices. The system must be capable of receiving CSV files sent by the provider containing device information.

#### Device Validation:
- Devices have the following characteristics: IMEI, STATUS, PROVIDER, SCORE, VALIDATION DATE.
- Devices must be sorted in ascending order based on the IMEI when read from the CSV file.
- Validate that the device provider exists in our database; if not, discard the validation.
- Only validate devices with a status of "READY_TO_USE".
- Only validate devices with a score greater than 60.
- Devices with a palindromic IMEI should not be validated.
- Validated devices must be stored in a database table with all device information, a validation ID, and the person who performed the upload.

#### Example CSV File:
    ```
    imei,status,score,validation_status,validation_date,supplier_id,employee_id
    5478810,CANCELLED,90,Validated,4/17/2024 08:37:23,4,4
    48619219,READY_TO_USE,96,Invalid,12/9/2023 15:22:01,1,1
    80792153,CANCELLED,48,Validated,2/3/2022 08:21:54,1,2
    42781275,CANCELLED,0,Validated,11/12/2023 13:36:33,2,3
    74964009,CANCELLED,4,Validated,10/16/2022 05:38:03,3,1
    42711578,READY_TO_USE,35,Invalid,4/11/2023 00:03:27,1,1
    48175181,READY_TO_USE,11,Validated,2/3/2024 17:38:21,1,4
    65398427,CANCELLED,0,Validated,4/9/2022 18:30:15,1,3
    2194899,READY_TO_USE,36,Validated,10/19/2023 18:02:44,4,3
    66430777,READY_TO_USE,52,Validated,6/30/2024 09:16:06,4,4
    ```

### Installation Instructions:
1. Clone the project using the URL: [https://github.com/eimybarrera/Action_factory.git](https://github.com/eimybarrera/Action_factory.git)
2. Create a SQL database using Workbench, you can use the name `action_factory`.
3. Open the project and locate the `application.properties` file in `src/main/resources`. Update it as follows:
    ```
    spring.application.name=ActionFactory

    # MySQL Configuration
    spring.datasource.url=jdbc:mysql://localhost:3306/action_factory
    spring.datasource.username=root
    spring.datasource.password=<YourPassword>
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```
4. Ensure the `spring.datasource.username` and `spring.datasource.password` are correct.
5. Navigate to the `ActionFactoryApplication` class located in `src/main/java/ActionFactoryApplication` and run it.
6. Send requests using Insomnia or Postman.

## Endpoints and Requests

### Supplier Controller

#### POST /api/v1/suppliers
Create a new supplier.

**Example:**

```
{
    "name": "Supplier One",
    "address": "123 Main St",
    "phoneNumber": "123-456-7890",
    "email": "supplier.one@example.com",
    "website": "www.supplierone.com",
    "industrySector": "Electronics",
    "registrationDate": "2023-01-01"
}
```
**Expected Response:**
```
Supplier created successfully
```
#### POST/api/v1/suppliers/list

**Example: **

### Usage Instructions:
1. Use Insomnia IDE to interact with the project.
2. Modify the `application.properties` file with your database details.
3. Run the application and use Insomnia or Postman to send requests.

### Features:
- Provider management
- Employee management
- Device validation

### Technologies Used:
- Java and Spring Framework for backend development.
- SQL or NoSQL databases for information storage.
- REST for web service creation.
- UML diagrams for system design.
- JPA for object-relational mapping.
- Continuous Integration and Deployment with GitHub Actions and GCP or AWS.
- JWT for user authentication and authorization.
- Swagger for API documentation.
- Design patterns and best practices for clean, maintainable, and scalable code.
- Unit tests creation.
- Cloud deployment.
- README documentation.
- GitHub project management.

## Authors
- Angie Guevara: https://www.linkedin.com/in/angie-guevara-developer/
- Eimy Garcia: https://www.linkedin.com/in/eimy-garcia-backend/
- Maria Camila Berrio: http://www.linkedin.com/in/maria-camila-berrio-alzate
- David Herrera: https://www.linkedin.com/in/david-herrera-back-developer/


