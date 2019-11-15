package com.organization.service;

import com.organization.entity.Designation;
import com.organization.entity.Employee;
import com.organization.entity.EmployeePost;
import com.organization.exception.BadRequestException;
import com.organization.exception.NotFoundException;
import com.organization.repository.DesignationRepository;
import com.organization.repository.EmployeeRepository;
import com.organization.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.text.WordUtils;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private DesignationRepository designationRepository;
    private MessageUtil messageUtil;

    @Autowired   // Constructor Injection
    EmployeeService(EmployeeRepository employeeRepository,DesignationRepository designationRepository,MessageUtil messageUtil)
    {
        this.employeeRepository=employeeRepository;
        this.designationRepository=designationRepository;
        this.messageUtil=messageUtil;
    }
/*-----------------------Method to get all Employees in database-------------------------------*/
    public List<Employee> getAllEmployee()
    {
        List<Employee> employeeList=employeeRepository.findAllByOrderByDesignation_LevelIdAscEmpNameAsc(); // Finding details of all Employee sorted by LevelId, EmployeeName
        if (employeeList.size() > 0)
        {
            return employeeList;  // Returning Non Empty List
        }
        else
        {
            throw new NotFoundException(messageUtil.getMessage("Validation.noRecord"));  // Returning Error Message For Empty List
        }

    }

/*---------------------Method to get Details of employee  provided Employee Id. This Method returns details of Employee, His Manager details
    * ,his colleague details and also the details of Employee reporting to that particular Employee --------------------------------------------------------------------*/
    public Map getEmployee(int  id) {
        if (id > 0)  // Checking if Id is positive or not
        {
            Map<String, Object> details = new LinkedHashMap<>();
            Optional<Employee> employeeTemp = employeeRepository.findById(id); // Getting Employee Details
            if(employeeTemp.isPresent())
            {
                Employee employee = employeeTemp.get();
                details.put("employee", employee);
                if (!(employee.getManagerId() == -1 || employee.getManagerId() == null))  // Checking for Valid ManagerId
                {
                    Optional<Employee> managerDetails = employeeRepository.findById(employee.getManagerId()); // Getting Manager Details
                    Employee managerValue = managerDetails.get();
                    details.put("manager", managerValue);
                }
                List<Employee> employeeList = employeeRepository.findAllByOrderByDesignation_LevelIdAscEmpNameAsc();
                List<Employee> colleague = new ArrayList<>(); // Creating List to get Colleague details
                for (int i = 0; i < employeeList.size(); i++)
                {
                    // Condition to get Colleague details
                    if (employeeList.get(i).getManagerId() == employee.getManagerId() && employee.getEmpId() != employeeList.get(i).getEmpId()){
                        colleague.add(employeeList.get(i));
                    }
                }
                if (colleague.size() != 0) //Checking the Number of Colleagues
                {
                    details.put("colleagues", colleague);
                }
                List<Employee> subordinate = new ArrayList<>(); // Creation of List to get Subordinate Details
                for (int i = 0; i < employeeList.size(); i++)
                {
                    if (employeeList.get(i).getManagerId() == employee.getEmpId()) // Condition for getting Subordinate details
                    {
                        subordinate.add(employeeList.get(i));
                    }
                }
                if (subordinate.size() != 0)  // Checking the number of Subordinates
                {
                    details.put("subordinates", subordinate);
                }
                return details; // Returning Employee Details for Valid Employee Id
            }
            else
            {
                throw new NotFoundException(messageUtil.getMessage("Validation.noEmployee"));
            }

        }
        else
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.id.notValid")); // Error Message With Http Response Msg for Invalid Employee Id
        }
    }

/*--------------------------Method to delete an employee from table provided with employee id -------------*/
    public String deleteEmployeeById(int id)
    {
        if(id>0) // Checking For Correct Id Type
        {
            Optional<Employee> employeeDetails = employeeRepository.findById(id);
            if (employeeDetails.isPresent()) {
                List<Employee> employeeList = employeeRepository.findAll();
                int c = 0;
                for (int i = 0; i < employeeList.size(); i++) {
                    if (employeeList.get(i).getManagerId() == employeeDetails.get().getEmpId()) // Counting Number of Subordinates
                    {
                        c = c + 1;
                    }
                }
                if (employeeDetails.get().getEmpId() == 1 & c >= 1) // More then One Subordinate exits so can't be deleted
                {
                    throw new BadRequestException(messageUtil.getMessage("Validation.directorWithSubordinate"));
                } else if (employeeDetails.get().getEmpId() == 1 && c == 0)  //Director with no Subordinate so it can be deleted
                {
                    employeeRepository.deleteById(id);
                    return (messageUtil.getMessage("Validation.directorWithNoSubordinate"));
                } else if (employeeDetails.get().getEmpId() != 1 && c == 0) // Employee with no subordinates can be deleted
                {
                    employeeRepository.deleteById(id);
                    return (messageUtil.getMessage("Validation.employeeWithNoSubordinate"));
                } else  //Employee with multiple Subordinates can be deleted, ManagerId of Subordinate changes
                {
                    int j = employeeDetails.get().getManagerId();
                    for (int i = 0; i < employeeList.size(); i++) {
                        if (employeeList.get(i).getManagerId() == employeeDetails.get().getEmpId()) // changing ManagerId of Subordinate
                        {
                            employeeList.get(i).setManagerId(j);
                            employeeRepository.save(employeeList.get(i));
                        }
                    }
                    employeeRepository.deleteById(id);
                    return (messageUtil.getMessage("Validation.employeeWithSubordinate"));
                }
            } else {
                throw new NotFoundException(messageUtil.getMessage("Validation.noEmployee"));
            }
        }
        else
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.id.notValid"));
        }
    }

/* ------------------------Method to add new employee (POST) ----------------------------------*/
    public Map addEmployee(EmployeePost employee) {
        Employee emp = new Employee();                                                            // Creating New Employee Object
        if (employee.getJobTitle() == null)                    // Checking If Employee Job Title is Passed in Request Body or Not
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.jobNull"));
        }
        if(employee.getEmpName() == null)                     // Checking if Employee Name is passed in Request Body or Not
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.nameNull"));
        }
        Pattern p = Pattern.compile("^[ A-Za-z]+$");         // Regex t check Employee Name Format
        Matcher m = p.matcher(employee.getEmpName());        // Matching New Employee Name With Regex
        boolean b = m.matches();
        if(b == false)                                      // Determining if Name Is In correct Format
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.wrongNameFormat"));
        }
        Designation designation = designationRepository.findByJobTitle(WordUtils.capitalize(employee.getJobTitle())); // Fetching Details of Designation From JobTile
                                                                                               // Using WordUtils.Capitalize to Capitalize first Letter of word in String
        if (designation == null)                                                               // checking if JobTitle exist in Database or not
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.jobTitleNotFound"));
        }
        List<Employee> employeeList= employeeRepository.findAll();
        emp.setEmpName(WordUtils.capitalize(employee.getEmpName().toLowerCase()));  // Setting Employee Name in Form "John Wick"
        emp.setDesignation(designation);       // Setting Employee Designation Details
        if(employeeRepository.findAll().size()!=0)
        {
            if (employee.getManagerId() == null)                       // if There is director then new Employee Manager Id cannot be Null
            {
                throw new BadRequestException(messageUtil.getMessage("Validation.managerIdNull"));
            }
            if (!employeeRepository.findById(employee.managerId).isPresent()){
                throw new BadRequestException(messageUtil.getMessage("Validation.managerNotFound"));
            }
            emp.setManagerId(employee.getManagerId());
            int newEmployeeLevelId = emp.getDesignation().getLevelId();   //Finding LevelId of New Employee to be Inserted
            List<Employee> allEmployee = employeeRepository.findAll();
            Employee parent = new Employee();
            for (int i = 0; i < allEmployee.size(); i++)
            {
                if (allEmployee.get(i).getEmpId() == employee.getManagerId())
                {
                    parent = allEmployee.get(i);
                }
            }
            int parentLevelId = parent.getDesignation().getLevelId();  // Finding the LevelId of Manager Of New Employee
            if (parentLevelId < newEmployeeLevelId)
            {
                employeeRepository.save(emp);                         //Saving new Employee details in Employee Repository

                return getEmployee(emp.getEmpId());
            }
            else if (newEmployeeLevelId == 1)
            {
                throw new BadRequestException(messageUtil.getMessage("Validation.directorExist"));
            }
            else
                {
                    throw new BadRequestException(messageUtil.getMessage("Validation.employeeInvalidDesignation"));
            }
        }
        else
        {
                if(employee.getManagerId()==-1 || employee.getManagerId()==null)
                {
                    emp.setManagerId(-1);                       // Manager Id In Case Of Very First employee can be null
                    employeeRepository.save(emp);                 // Saving The Employee Details in Repository
                    return getEmployee(emp.getEmpId());

                }
                else
                {
                    throw new BadRequestException(messageUtil.getMessage("Validation.firstEmployeeNoManager"));
                }
        }
    }
 /*---------------------- Method to Update an Employee Details (PUT) ---------------------------------------*/
    public Map updateEmployeeDetails(int id,EmployeePost employee)
    {
        if((employee.getEmpName()==null&&employee.jobTitle==null&&employee.getManagerId()==null)||(employee.getEmpName()==""&&employee.jobTitle==""&&employee.getManagerId()==null)) // Checking If Request Body is Null
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.responseBody"));    // if Condition to check if If Request Body is NUll
        }
        Employee employeeDetails = new Employee();
        List<Employee> employeeList= employeeRepository.findAll();
        if(id > 0)                               // Checking for Valid Employee Id
        {
            for (int i = 0; i < employeeList.size(); i++) {
                if (employeeList.get(i).getEmpId() == id) {
                    employeeDetails = employeeList.get(i);                      // Getting Employee Details after getting EmpId Passed in json body
                }
            }
            if(employeeDetails.getEmpId() == null)                        // Finding If Employee For Given Id is Present or Not
            {
                throw new BadRequestException(messageUtil.getMessage("Validation.noEmployee"));
            }
        }
        else
        {
            throw new BadRequestException(messageUtil.getMessage("Validation.id.notValid"));  // Invalid Manager Id Passed
        }
        if(employee.getReplace()==false) // Updating the Details of employee with given Changes
        {
            if(employee.getEmpName()!=null) {
                Pattern p = Pattern.compile("^[ A-Za-z]+$");            // Regex t check Employee Name Format
                Matcher m = p.matcher(employee.getEmpName());           // Matching New Employee Name With Regex
                boolean b = m.matches();
                if(b==true){
                    employeeDetails.setEmpName(WordUtils.capitalize(employee.getEmpName()));  //Setting Employee Name To New Name as Requested
                }
                else{
                    throw new BadRequestException(messageUtil.getMessage("Validation.wrongNameFormat"));  //Name Format is Wrong
                }
            }
            else
            {
                employeeDetails.setEmpName(employeeDetails.getEmpName());   // If New Name is Not provide Name is set to previous name
                employeeRepository.save(employeeDetails);
            }
            if(employee.getJobTitle()!=null) {                           // Condition to Check Job Title in Request
                if (employeeDetails.getJobTitle().equals("Director")) { //  checking if Director Designation Can be Updated
                    throw new BadRequestException(messageUtil.getMessage("Validation.directorDesignationCannotUpdated"));
                }
                Designation designation = designationRepository.findByJobTitle(WordUtils.capitalize(employee.getJobTitle())); // fetching designation Details From Passed new job Title after setting job Title to DB format
                if (designation == null) {                                                                   // if Job Title Exit or not
                    throw new BadRequestException(messageUtil.getMessage("Validation.jobTitleNotFound"));
                }
                int newLevelId = designation.getLevelId();
                Optional<Employee> manager = employeeRepository.findById(employeeDetails.getManagerId());             // Getting Manager Details
                int parentLevelId = manager.get().getDesignation().getLevelId();
//                int currentLevelId = employeeDetails.getDesignation().getLevelId();
                List<Employee> subordinate = new ArrayList<>();
                List<Employee> employeeListForSubordinates = employeeRepository.findAllByOrderByDesignation_LevelIdAscEmpNameAsc();
                for (int i = 0; i < employeeListForSubordinates.size(); i++) {
                    if (employeeListForSubordinates.get(i).getManagerId() == employeeDetails.getEmpId())               //Getting Subordinated of Employee in Order to Determine Till where Employee Designation can be Degraded
                    {
                        subordinate.add(employeeListForSubordinates.get(i));
                    }
                }
                if (subordinate.isEmpty()) {                                                             //If Emloyee Has No Subordinates Then designation can Be Degraded to lowest Designation
                    if ((newLevelId > parentLevelId)) {
                        employeeDetails.setDesignation(designationRepository.findByJobTitle(WordUtils.capitalize(employee.getJobTitle())));  // Setting The New Designation for Employee
                        employeeDetails.setJobTitle(WordUtils.capitalize(employee.getJobTitle()));
                        employeeRepository.save(employeeDetails);
                    }
                    else
                    {
                        throw new BadRequestException(messageUtil.getMessage("Validation.levelMatchError"));
                    }
                }
                else
                    {
                    int permittedLevelId=subordinate.get(0).getDesignation().getLevelId();
                    if ((newLevelId > parentLevelId) && (newLevelId < permittedLevelId))                                         //Comparing the New level Id with Employee Manager Id and his current level id
                    {                                                                                                   // Assumption Employee Cannot given Lower designation
                        employeeDetails.setDesignation(designationRepository.findByJobTitle(WordUtils.capitalize(employee.getJobTitle())));  // Setting The New Designation for Employee
                        employeeDetails.setJobTitle(WordUtils.capitalize(employee.getJobTitle()));
                        employeeRepository.save(employeeDetails);

                    } else {
                        throw new BadRequestException(messageUtil.getMessage("Validation.levelMatchError"));
                    }
                }
            }
            else {
                employeeDetails.setJobTitle(employeeDetails.getJobTitle());
                employeeRepository.save(employeeDetails);
            }
            if (employee.getManagerId()!= null)
            {
                Optional<Employee> managerDetails = employeeRepository.findById(employee.getManagerId());
                if (!managerDetails.isPresent())                               // Checking for Invalid Manager Id
                {
                    throw new BadRequestException(messageUtil.getMessage("Validation.managerNotFound"));
                }
                Designation designation = designationRepository.findByJobTitle(managerDetails.get().getJobTitle());
                int managerLevelId = designation.getLevelId();
                int employeeLevelId = employeeDetails.getDesignation().getLevelId();
                if (managerLevelId < employeeLevelId)   // Matching Designation Details Of New Manager and Employee
                {
                    employeeDetails.setManagerId(employee.getManagerId());
                    employeeRepository.save(employeeDetails);
                }
                else
                    {
                        throw new BadRequestException(messageUtil.getMessage("Validation.managerLevelIdMatch"));
                    }
            }
            else
                {
                employeeDetails.setManagerId(employeeDetails.getManagerId());
                employeeRepository.save(employeeDetails);
               }
            return (getEmployee(employeeDetails.getEmpId()));     // Returning details of Employee after changing the details
        }
        else    // Replacing the Old Employee with New Employee
            {
              Employee newEmployee= new Employee();
              Designation designation = designationRepository.findByJobTitle(WordUtils.capitalize(employee.getJobTitle())); // fetching designation Details From Passed new job Title after setting job Title to DB format
              if(designation==null)
              {
                  throw new BadRequestException(messageUtil.getMessage("Validation.jobNull"));
              }
              if(employee.getEmpName()!=null) {
                  Pattern p = Pattern.compile("^[ A-Za-z]+$");
                  Matcher m = p.matcher(employee.getEmpName());
                  boolean b = m.matches();
                  if (b == false) {
                      throw new BadRequestException(messageUtil.getMessage("Validation.wrongNameFormat"));
                  }
              }
              else{
                  throw new BadRequestException(messageUtil.getMessage("Validation.nameNull"));
              }
             int newLevelId1=designation.getLevelId(); // Employee Level Id
              Optional<Employee> manager= employeeRepository.findById(employeeDetails.getManagerId());
              int managerLevelId = manager.get().getDesignation().getLevelId();
              List<Employee> subordinate = new ArrayList<>();
              List<Employee> employeeListForSubordinates = employeeRepository.findAllByOrderByDesignation_LevelIdAscEmpNameAsc();
              for (int i = 0; i < employeeListForSubordinates.size(); i++) {
                    if (employeeListForSubordinates.get(i).getManagerId() == employeeDetails.getEmpId())               //Getting Subordinated of Employee in Order to Determine Till where Employee Designation can be Degraded
                    {
                        subordinate.add(employeeListForSubordinates.get(i));
                    }
                }
              int permittedLevelId=subordinate.get(0).getDesignation().getLevelId();            // Calculation Of Threshold Level Id
              if(!subordinate.isEmpty()) {
                      if (newLevelId1 > managerLevelId && newLevelId1 < permittedLevelId + 1) {
                      newEmployee.setEmpName(WordUtils.capitalize(employee.getEmpName().toLowerCase()));
                      newEmployee.setDesignation(designation);
                      if (employee.getManagerId() != null) {
                          newEmployee.setManagerId(employee.getManagerId());                   // Setting Manager id to New Manager Id
                      } else {
                          newEmployee.setManagerId(employeeDetails.getManagerId());           // Setting Manager Id to Manager Id of Old Employee
                      }
                      employeeRepository.save(newEmployee);
                      List<Employee> subordinateList = new ArrayList<>();     // Conditions for changing the ManagerId of Subordinates
                      for (int i = 0; i < employeeList.size(); i++) {
                          if (employeeList.get(i).getManagerId() == employeeDetails.getEmpId()) {
                              subordinateList.add(employeeList.get(i));
                          }
                      }
                      for (int i = 0; i < subordinateList.size(); i++) {
                          subordinateList.get(i).setManagerId(newEmployee.getEmpId());
                          employeeRepository.save(subordinateList.get(i));
                      }

                      employeeRepository.deleteById(employeeDetails.getEmpId()); // Deleting the old Employee
                          return getEmployee(newEmployee.getEmpId());
                  } else {
                          throw new BadRequestException(messageUtil.getMessage("Validation.employeeInvalidDesignation"));
                  }
              }
              else                                            // Condition for Setting Manager Id in case of No Subordinates
               {
                  if (newLevelId1 > managerLevelId)
                  {
                      newEmployee.setEmpName(WordUtils.capitalize(employee.getEmpName().toLowerCase()));
                      newEmployee.setDesignation(designation);
                      if (employee.getManagerId() != null){
                          newEmployee.setManagerId(employee.getManagerId());
                      }
                      else {
                          newEmployee.setManagerId(employeeDetails.getManagerId());
                      }
                      employeeRepository.save(newEmployee);
                      List<Employee> subordinateList = new ArrayList<>();     // Conditions for changing the ManagerId of Subordinates
                      for (int i = 0; i < employeeList.size(); i++) {
                          if (employeeList.get(i).getManagerId() == employeeDetails.getEmpId()) {
                              subordinateList.add(employeeList.get(i));
                          }
                      }
                      for (int i = 0; i < subordinateList.size(); i++) {
                          subordinateList.get(i).setManagerId(newEmployee.getEmpId());
                          employeeRepository.save(subordinateList.get(i));
                      }
                      employeeRepository.deleteById(employeeDetails.getEmpId()); // Deleting the old Employee
                      return getEmployee(newEmployee.getEmpId());
                  }
                  else {
                      throw new BadRequestException(messageUtil.getMessage("Validation.employeeInvalidDesignation"));
                  }
              }
        }
    }
}

