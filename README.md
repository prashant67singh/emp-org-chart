# Employee Management System

A Spring Boot project for Employee Management System

## Table of Contents
- [Validation Script](#validation-script)
- [Database](#database)
    - [Designation](#designation)
    - [Employee](#employee)
- [Example Date](#example-data)
    - [Designation](#designation)
    - [Employee](#employee)
- [API](#api)
    
## Validation Script

### Setup
- Download JQ from [here](https://github.com/stedolan/jq/releases/download/jq-1.6/jq-win64.exe)
- Copy exe file to the GitBash folder (Example: `C:\Users\<username>\Programs\Git`)

### Run
```
git-bash
./main.sh
```

### Environment Variables

- **SERVER_HOST**: Host of server (Default: localhost)
- **SERVER_PORT**: Port of server (Default: 8080)
- **SERVER_API_PREFIX**: Prefix of server API (Default: /api/v1)
## Database

#### Designation
- id: Integer (Primary Key)
- levelId: Integer
- jobTitle: String

#### Employee
- id: Integer (Primary Key)
- name: String
- managerId: Integer
- designation: Designation (Reference)

### Example Data

Designation

| id  | levelId | jobTitle     |
| --- | ------- | ------------ |
| 1   | 1       | Director     |
| 2   | 2       | Manager      |
| 3   | 3       | Lead         |
| 4   | 4       | Developer    |
| 5   | 4       | DevOps       |  
| 6   | 4       | QA           |
| 7   | 5       | Intern       |

Employee

| id  | name            | managerId | designation   |
| --- | --------------- | -------   | ------------- |
| 1   | Thor            | null      | 1 (Director)  |
| 2   | Iron Man        | 1         | 2 (Manager)   |
| 3   | Hulk            | 1         | 3 (Lead)      |
| 4   | Captain America | 1         | 2 (Manager)   |
| 5   | War Machine     | 2         | 6 (QA)        |
| 6   | Vision          | 2         | 5 (DevOps)    |
| 7   | Falcon          | 4         | 4 (Developer) |
| 8   | Ant Man         | 4         | 3 (Lead)      |
| 9   | Spider Man      | 2         | 7 (Intern)    |
| 10  | Black kWidow    | 3         | 4 (Developer) |

## API

#### Error Codes
- 200: OK
- 201: Created
- 400: Bad Request
- 403: Forbidden
- 404: Resource Not Found
- 405: Method Not Allowed
- 406: Not Acceptable

#### GET /employees

Returns list of all employees in Employee Database

Request
```
GET /employees
```

Response
```json
[
    {
        "jobTitle": "Director",
        "id": 1,
        "name": "Thor"
    },
    {
        "jobTitle": "Manager",
        "id": 4,
        "name": "Captain America"
    },
    {
        "jobTitle": "Manager",
        "id": 2,
        "name": "Iron Man"
    },
    {
        "jobTitle": "Lead",
        "id": 8,
        "name": "Ant Man"
    },
    {
        "jobTitle": "Lead",
        "id": 3,
        "name": "Hulk"
    },
    {
        "jobTitle": "Developer",
        "id": 10,
        "name": "Black Widow"
    },
    {
        "jobTitle": "Developer",
        "id": 7,
        "name": "Falcon"
    },
    {
        "jobTitle": "DevOps",
        "id": 6,
        "name": "Vision"
    },
    {
        "jobTitle": "QA",
        "id": 5,
        "name": "War Machine"
    },
    {
        "jobTitle": "Intern",
        "id": 9,
        "name": "Spider Man"
    }
]
```

### POST /employees

Add a new employee to Employee Database

Body
```json
{
  "name": "Employee Name - String",
  "jobTitle": "Employee Designation - String",
  "managerId": "Manager Employee ID, Required in case current employee is not Director - must be Integer "
}
```

Request
```
POST /employees
body: {
    "name": "Dr Strange",
    "jobTitle": "Manager",
    "managerId": 1
}
```

Response

```json
{
    "employee": {
        "jobTitle": "Manager",
        "id": 11,
        "name": "Dr Strange"
    },
    "manager": {
        "jobTitle": "Director",
        "id": 1,
        "name": "Thor"
    },
    "colleagues": [
        {
            "jobTitle": "Manager",
            "id": 4,
            "name": "Captain America"
        },
        {
            "jobTitle": "Manager",
            "id": 2,
            "name": "Iron Man"
        },
        {
            "jobTitle": "Lead",
            "id": 3,
            "name": "Hulk"
        }
    ]
}
```

### GET employees/{id}

Returns Data of specific Employee For Enquired Employee Id

Request
```
GET /employees/2
```

Response
```json
{
    "employee": {
        "jobTitle": "Manager",
        "id": 2,
        "name": "Iron Man"
    },
    "manager": {
        "jobTitle": "Director",
        "id": 1,
        "name": "Thor"
    },
    "colleagues": [
        {
            "jobTitle": "Manager",
            "id": 4,
            "name": "Captain America"
        },
        {
            "jobTitle": "Lead",
            "id": 3,
            "name": "Hulk"
        }
    ],
    "subordinates": [
        {
            "jobTitle": "DevOps",
            "id": 6,
            "name": "Vision"
        },
        {
            "jobTitle": "QA",
            "id": 5,
            "name": "War Machine"
        },
        {
            "jobTitle": "Intern",
            "id": 9,
            "name": "Spider Man"
        }
    ]
}
```

#### PUT /employees/${id}

Update or Replace Employee Details by Employee Id

Body
```json
{
  "name": "Employee Name - String",
  "jobTitle": "Employee Designation or Job Title - String",
  "managerId": "Manager Employee Id, Required if current employee is not Director - Integer",
  "replace": "Replace old employee with current employee - Boolean"
}
```

Request
```
PUT /employees/3
body: {
    "name": "Black Panther",
    "jobTitle": "Lead",
    "managerId": 1,
    "replace": true
}
```

Response
```json
{
    "employee": {
        "jobTitle": "Lead",
        "id": 11,
        "name": "Black Panther"
    },
    "manager": {
        "jobTitle": "Director",
        "id": 1,
        "name": "Thor"
    },
    "colleagues": [
        {
            "jobTitle": "Manager",
            "id": 4,
            "name": "Captain America"
        },
        {
            "jobTitle": "Manager",
            "id": 2,
            "name": "Iron Man"
        }
    ],
    "subordinates": [
        {
            "jobTitle": "Developer",
            "id": 10,
            "name": "Black Widow"
        }
    ]
}
```

### DELETE /employees/${id}

Delete Employee from Employee Database By his Id

Request
```
DELETE /employees/10
```

Response
```
OK
```
