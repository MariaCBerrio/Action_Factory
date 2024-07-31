<h1 align="center">ACTION FACTORY</h1>

![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Portada%20Action%20Factory%20(1).png)
## Project Context
In recent years, we have witnessed astonishing changes in our world, where technology has evolved at a dizzying pace. We live in an era where reality is one step closer to surpassing fiction each day, and all this innovation is at our fingertips; every day, from our mobile devices, we can perform tasks that we once could only imagine.

In this context of constant advancement, an imperative need arises: to have devices that enable efficiency and precision in every aspect of our daily operations. Thus, a challenge also emerges for companies: to efficiently and automatically validate large quantities of devices before their commercialization.

This is where **Action Factory** comes into play, an application developed to meet this need by applying the knowledge acquired during the BeTek Back End training program.


## Project Description
Action Factory seeks to revolutionize the validation process for devices from allied providers. This automated and robust system will efficiently and accurately manage the information of business partners and devices, freeing employees from repetitive manual tasks. 

## UML Diagram
![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/UML_Action_Factory.drawio.png)


## Functionalities:
1. **Employee Managemente:** The system will manage the employees of Action Factory, where employees can be created and assigned specific roles that allow them to perform tasks effectively and responsibly.
3. **Creation and Update of Suppliers**: Only employees with the coordinator role can perform this action.
4. **Device Management**.

### Roles:
- **Coordinator**: Has the authority to add, update, and validate companies, and to execute and supervise device validations.
- **Validator**: An expert in device validation, executing validations with precision and thoroughness.

### Validation Management:
The core of our company is facilitating the validation and management of devices. The system must be capable of receiving CSV files sent by the provider containing device information.

## Flowchart Diagram
![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Flowchart%20Diagram.jpg)

## Entity Relationship Model
![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Entity-Relationship%20Model.jpg)

### Arquitecture Diagram
![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Arquitechture%20Diagram.jpg)

#### Device Validation:
- Devices have the following characteristics: IMEI, STATUS, SCORE, VALIDATION_STATUS, VALIDATION DATE, SUPPLIER_ID, EMPLOYEE_ID.
- Devices must be sorted in ascending order based on the IMEI when read from the CSV file.
- Validate that the device provider exists in our database; if not, discard the validation.
- Only validate devices with a status of "READY_TO_USE".
- Only validate devices with a score greater than 60.
- Devices with a palindromic IMEI should not be validated.
- Validated devices must be stored in a database table with all device information, a validation ID, and the person who performed the upload.

#### Example CSV File:
    ```
	imei,status,score,validation_status,validation_date,supplier_id,employee_id
	670285535,READY_TO_USE,96,PENDING_VALIDATION,1/1/2023 23:30:28,3,5
	696183173,DAMAGED,95,PENDING_VALIDATION,6/3/2024 22:28:57,3,1
	911317525,READY_TO_USE,75,PENDING_VALIDATION,1/6/2021 02:44:36,4,2
	747846043,READY_TO_USE,99,PENDING_VALIDATION,2/6/2024 21:40:48,1,5
	409440451,READY_TO_USE,57,PENDING_VALIDATION,7/14/2023 03:47:24,3,3
	421210682,USED,89,PENDING_VALIDATION,9/16/2021 04:32:10,1,3
	662613308,OBSELETE,94,PENDING_VALIDATION,5/16/2021 15:06:58,1,1
	403821274,USED,71,PENDING_VALIDATION,11/3/2021 09:44:16,3,4
	601290485,DAMAGED,55,PENDING_VALIDATION,1/19/2022 17:48:43,4,3
	266290590,DAMAGED,94,PENDING_VALIDATION,6/30/2022 20:28:54,1,2
    ```

### Installation Instructions:
1. Clone the project using the URL: [](https://github.com/MariaCBerrio/Action_Factory.git)
2. Open the project using insominia and load the gradle file.
5. Navigate to the `ActionFactoryApplication` class located in `src/main/java/ActionFactoryApplication` and run it.
6. Send requests using Insomnia or Postman.

## Endpoints and Requests
### Role Controller
#### Post /api/v1/roles
This endpoint allows to create the roles in the database, since the program is designed with some specific funcionalities for the coordinator, it is recommended to define this role in the company.

**Example:**
```
	{
	"name": "Validator"
	}
```
```
	{
	"name":"Coordinator"
	}
```
#### Get /api/v1/roles
This endpoint allows to get a list of the roles defined in the Company
**Expected Response**
```
[
	{
		"id": 1,
		"name": "Validator"
	},
	{
		"id": 2,
		"name": "Coordinator"
	}
]
```
### Employee Controller
#### Post /api/v1/employees
This endpoint allows to create a new employee in the company

**Example:**
```
{
	"identificationCard": 123456,
	"name": "John Doe",
    	"email": "john.doe@example.com",
    	"password": "password123",
    	"hireDate": "2023-01-15",
    	"lastAccess": "2023-07-15T14:30:00",
    	"status": "active",
    	"role": {
        	"id": 1
    	}
}

```
#### Post /api/v1/employees/list
This endpoint allows to send a list of employees
**Example:**
```
[{
        "identificationCard": 234567,
        "name": "Jane Smith",
        "email": "jane.smith@example.com",
        "password": "password456",
        "hireDate": "2022-11-23",
        "lastAccess": "2023-06-10T09:15:00",
        "status": "inactivo",
        "role": {
            "id": 2
        }
    },
    {
        "identificationCard": 345678,
        "name": "Robert Johnson",
        "email": "robert.johnson@example.com",
        "password": "password789",
        "hireDate": "2021-03-05",
        "lastAccess": "2023-07-20T11:45:00",
        "status": "activo",
        "role": {
            "id": 1
        }
    }
]
```
#### GET /api/v1/employees
This endpoint allows to get a list of all the employees in the company.

#### DELETE /api/v1/employees/{id}
This endpoint allows to delete an employee using its id
**Example:** `/api/v1/employees/1`
**Expected:**
```
employee successfully deleted
```

### Supplier Controller

#### POST /api/v1/suppliers
Create a new supplier, to be able to create a new supplier the database must already contain at least one employee object who has a coordinator role.

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
	"employee": {
    	"id": 2
  }
}
```
**Expected Response:**
```
Supplier created successfully
```
#### POST/api/v1/suppliers/list
With this api we can update a list of suppliers.
**Example:**
```
[{
	"name": "Supplier One",
	"address": "123 Main St",
	"phoneNumber": "123-456-7890",
	"email": "supplier.one@example.com",
	"website": "www.supplierone.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-01-01",
	"employee": {
    	"id": 2
  }
},

{
	"name": "Gadget Distributors",
	"address": "456 Gadget Lane",
	"phoneNumber": "987-654-3210",
	"email": "info@gadgetdistributors.com",
	"website": "www.gadgetdistributors.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-02-15",
	"employee": {
    	"id": 2
  	}
},

 {
	"name": "Component World",
	"address": "789 Component Ave",
	"phoneNumber": "555-123-4567",
	"email": "support@componentworld.com",
	"website": "www.componentworld.com",
	"industrySector": "Hardware",
	"registrationDate": "2023-03-10",
	"employee": {
   	"id": 2
}
},

 {
	"name": "Device Experts",
	"address": "987 Device Blvd",
	"phoneNumber": "222-333-4444",
	"email": "info@deviceexperts.com",
	"website": "www.deviceexperts.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-05-30",
	"employee": {
    	"id": 2
  }
}]

```
**Expected Response:**
```
Suppliers created successfully
```
#### GET/api/v1/suppliers/{id}

**Example:** `/api/v1/suppliers/1`

**Expected Response:**
```
{
	"id": 1,
	"name": "Supplier One",
	"address": "123 Main St",
	"phoneNumber": "123-456-7890",
	"email": "supplier.one@example.com",
	"website": "www.supplierone.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-01-01",
	"employee": {
    	"id": 2
  }
}
```
#### PUT /api/v1/suppliers/{id}
This endpoint allows to modify specific information about a especific  supplier using his id.

**Example:** `/api/v1/suppliers/1`
```
 {
	"name": "Supplier One Modified",
	"address": "123 new Adress",
	"phoneNumber": "123-456-7850",
	"email": "supplier.one@example.com",
	"website": "www.supplierone.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-01-01",
	"employee": {
    	"id": 2
  }
}
```
**Expected Response:** 
```Supplier updated successfully```

#### Delete /api/v1/suppliers/{id}
This endpoint allows to delete a supplier using the id, all the related devices will also be deleted.

**Example:** `/api/v1/suppliers/1`

**Expected Response:**
```
Supplier deleted successfully
```

#### Get /api/v1/suppliers
This request allows to delete get a list of all the existing suppliers

**Example:** `/api/v1/suppliers`

**Expected Response:**
```
[{
	“id”: 2,
	"name": "Gadget Distributors",
	"address": "456 Gadget Lane",
	"phoneNumber": "987-654-3210",
	"email": "info@gadgetdistributors.com",
	"website": "www.gadgetdistributors.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-02-15"
	"employee": {
    	"id": 2
  	}
},

 {
	“id”: 3,
	"name": "Component World",
	"address": "789 Component Ave",
	"phoneNumber": "555-123-4567",
	"email": "support@componentworld.com",
	"website": "www.componentworld.com",
	"industrySector": "Hardware",
	"registrationDate": "2023-03-10"
	"employee": {
    	"id": 2
  	}
},

 {
	“id”: 4,
	"name": "Device Experts",
	"address": "987 Device Blvd",
	"phoneNumber": "222-333-4444",
	"email": "info@deviceexperts.com",
	"website": "www.deviceexperts.com",
	"industrySector": "Electronics",
	"registrationDate": "2023-05-30"
	"employee": {
    	"id": 2
  	}
}]

```
### Device Controller

#### POST /api/v1/devices/csv
Allows to upload a csv document of devices to the database, it must be charged as a multipart file, in the project description there is already an example of csv file, it can be used.

![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Devices%20Request.png)

### Technologies Used:
- **Back End Development:** Java 17 and Spring Boot Framework
- **Database:** MySQL
- **Continuous Integration:** GitHub Actions
- **Continuous Deployment:** Railway 
- **Api Documentation:** Swagger
- **Integrated Development Environment (IDE):** IntelliJ
- **API Testing and development platform:** Insomnia

![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Used%20Technologies.png)

## Authors
![](https://github.com/MariaCBerrio/Action_Factory/blob/main/Action%20Factory%20Images/Teamwork.png)
- Maria Camila Berrio: http://www.linkedin.com/in/maria-camila-berrio-alzate
- Eimy Garcia: https://www.linkedin.com/in/eimy-garcia-backend/
- Angie Guevara: https://www.linkedin.com/in/angie-guevara-developer/
- David Herrera: https://www.linkedin.com/in/david-herrera-back-developer/

## Acknowledgements

We would like to extend our heartfelt gratitude to BeTek for the comprehensive training and invaluable guidance provided throughout our journey. The knowledge and skills acquired during the Back End training program have been instrumental in the development of Action Factory.

### Discover BeTek

If you are passionate about technology and eager to advance your career, we highly recommend exploring the educational opportunities offered by BeTek. Their programs are designed to equip you with the practical skills and knowledge needed to excel in the tech industry.

Visit [BeTek](https://betek.la/) to learn more about their courses and how they can help you achieve your professional goals.

Thank you, BeTek, for empowering us to make a difference through technology!

