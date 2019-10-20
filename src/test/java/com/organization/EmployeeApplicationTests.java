package com.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.organization.controller.EmployeeController;
import com.organization.entity.EmployeePost;
import com.organization.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {EmployeeApplication.class, EmployeeController.class})

public class EmployeeApplicationTests extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    MockMvc mockMvc;

    private String path = "/employees";

    @Autowired
    EmployeeRepository employeeRepository;
    // Test GetAllEmployee
    @Test
        public void getAllEmployeeTest() throws Exception{
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Thor"))
                .andExpect(jsonPath("$[0].jobTitle").value("Director"));
    }

    // Test if getAllEmployee method returns  empty list
    @Test
    public void getAllEmployeeNullTest() throws  Exception{
        employeeRepository.deleteAll();
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    //Test the Response Status of GetAll Employee method
    @Test
    public void getAllEmployeeStatusTest() throws Exception{
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //Test getEmployeeById and also check its Response status
    @Test
    public void testGetEmployeeById() throws Exception {
        mockMvc.perform(get(path+"/{id}",1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    //Test getEmployee( int id) for if id is negative
    @Test
    public void testGetEmployeeByIdForInvalidEmployeeId() throws Exception{
        mockMvc.perform(get(path+"/{id}",-1))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test getEmployee(Int Id) for Invalid empId
    @Test
    public void testGetEmployeeByForEmployeeIdNotPresent() throws Exception{
        mockMvc.perform(get(path+"/{id}",20))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Test deleteEmployeeById(int id) if id passed as argument is Valid or Not
    @Test
    public void testDeleteEmployeeByIdForInvalidNegativeEmployeeId() throws Exception{
        mockMvc.perform(delete(path+"/{id}",-2))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test deleteEmployeeById(int id) if Employee Is not present for given Employee Email Id
    @Test
    public void testDeleteEmployeeByIdForEmployeeIdNotPresent() throws Exception{
        mockMvc.perform(delete(path+"/{id}",20))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Test deleteEmployeeById(int id) along with its Response Status for Employee with no subordinates
    @Test
    public void testDeleteEmployeeByIdWithNoSubordinates() throws Exception{
        mockMvc.perform(delete(path+"/{id}",10))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // Test deleteEmployeeById(int id) along with its Response Status for Employee with multiple subordinates
    @Test
    public void testDeleteEmployeeByIdWithSubordinates() throws Exception{
        mockMvc.perform(delete(path+"/{id}",3))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    //Test deleteEmployeeById(int id) for deleting Director with multiple Subordinates
    @Test
    public void TestForDeletingDirectorWithSubordinates() throws Exception{
        mockMvc.perform(delete(path+"/{id}",1))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //Test deleteEmployeeById(int id) for deleting Director without  any Subordinates
    @Test
    public void TestForDeletingDirectorWithoutSubordinates() throws Exception{
        for (int i=2;i<=10;i++){
            employeeRepository.deleteById(i);
        }
        mockMvc.perform(delete(path+"/{id}",1))
                .andDo(print())
                .andExpect(status().isOk());
    }
    //Test addEmployee() for correct Json Body
    @Test
    public void TestForAddEmployee() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(1);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // Test addEmployee() for null Manager Id
    @Test
    public void TestForAddEmployeeForNullManagerId() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
       employee.setManagerId(-1);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test addEmployee() for null Employee Name
    @Test
    public void TestForAddEmployeeForNullEmployeeName() throws Exception{
        EmployeePost employee =new EmployeePost();
//        employee.setEmpName("Prashant");
        employee.setManagerId(1);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test addEmployee() for null Employee JobTitle
    @Test
    public void TestForAddEmployeeForNullJobTile() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(1);
//        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test addEmployee() , if Designation is assigned is not present in the  Designation Table
    @Test
    public void TestForAddEmployeeForInValidDesignation() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(1);
       employee.setJobTitle("Supervisor");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    // Test addEmployee() , Another Director is Added if One Director already Exist
    @Test
    public void TestForAddEmployeeForAddingAnotherDirector() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(2);
        employee.setJobTitle("Director");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test addEmployee() , Adding of Employee with Higher Or Same level Id as of his Manager
    @Test
    public void TestForAddEmployeeForAddingEmployeeWithHigherOrEqualDesgnLevel() throws Exception{
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(2);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test addEmployee() , Adding first Employee in EMS with Null ManagerId
    @Test
    public void TestForAddEmployeeForAddingFirstEmployeeWithNullManagerId() throws Exception{
        employeeRepository.deleteAll();
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(-1);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    // Test addEmployee() , Adding first Employee in EMS with Some ManagerId
    @Test
    public void TestForAddEmployeeForAddingFirstEmployeeWithManagerId() throws Exception{
        employeeRepository.deleteAll();
        EmployeePost employee =new EmployeePost();
        employee.setEmpName("Prashant");
        employee.setManagerId(2);
        employee.setJobTitle("Manager");
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test updateEmployeeDetails() for checking validity of Employee Id Passed
    @Test
    public  void testUpdateEmployeeDetailsForValidEmployeeId() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Manager");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",-2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    // Test updateEmployeeDetails() for checking of Employee Id Passed is present in Table
    @Test
    public  void testUpdateEmployeeDetailsForEmployeeIdNotPresent() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Manager");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",20)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    // Test updateEmployeeDetails() for Replace=false
    @Test
    public  void testUpdateEmployeeDetailsForReplaceFalse() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("DevOps");
        employee.setManagerId(4);
        employee.setReplace(false);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",9)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // Test updateEmployeeDetails() for Replace=false and Invalid Job Title
    @Test
    public  void testUpdateEmployeeDetailsForReplaceFalseInvalidJobTitle() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Developers");
        //employee.setManagerId(4);
        employee.setReplace(false);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test updateEmployeeDetails() for Replace=false and Designation Is Higher Then His Manager Designation or New Designation Is Lower Then His Current Designation
    @Test
    public  void testUpdateEmployeeDetailsForReplaceFalseJobTitleHighOrLow() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Manager");
        //employee.setManagerId(4);
        employee.setReplace(false);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    // Test updateEmployeeDetails() for Replace=false and Invalid Manager Id
    @Test
    public  void testUpdateEmployeeDetailsForReplaceFalseInvalidManagerId() throws Exception{
        EmployeePost employee = new EmployeePost();
        //employee.setEmpName("Prashant Singh");
        // employee.setJobTitle("Manager");
        employee.setManagerId(20);
        employee.setReplace(false);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test updateEmployeeDetails() for Replace=false and New Manager Id cannot be Assigned
    @Test
    public  void testUpdateEmployeeDetailsForReplaceFalseManagerIdWithSameOrLowerDesignation() throws Exception{
        EmployeePost employee = new EmployeePost();
        //employee.setEmpName("Prashant Singh");
        // employee.setJobTitle("Manager");
        employee.setManagerId(6);
        employee.setReplace(false);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    // Test updateEmployeeDetails() for Replace=true and also test response status
    @Test
    public  void testUpdateEmployeeDetailsForReplaceTrue() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Manager");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk());
    }


    // Test updateEmployeeDetails() for Replace=true and Employee name = Null
    @Test
    public  void testUpdateEmployeeDetailsForReplaceTrueEmpNameNull() throws Exception{
        EmployeePost employee = new EmployeePost();
        //employee.setEmpName("");
        employee.setJobTitle("Manager");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test updateEmployeeDetails() for Replace=true and JobTitle = Null
    @Test
    public  void testUpdateEmployeeDetailsForReplaceTrueJobTitleNull() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
       // employee.setJobTitle("Manager");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test updateEmployeeDetails() for Replace=true and Designation is either Higher or Lower
    @Test
    public  void testUpdateEmployeeDetailsForReplaceTrueDesignationHighLow() throws Exception{
        EmployeePost employee = new EmployeePost();
        employee.setEmpName("Prashant Singh");
        employee.setJobTitle("Developer");
        employee.setManagerId(1);
        employee.setReplace(true);
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter =objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson=objectWriter.writeValueAsString(employee);
        mockMvc.perform(put(path+"/{id}",2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk());
    }









}