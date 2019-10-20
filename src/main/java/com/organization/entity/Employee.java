package com.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table
public class Employee {


    @Id                                               // Declaring Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // For generating Id Automatically
    @JsonProperty("id")                              // Providing Alias Name
    private Integer empId;
    @JsonProperty("name")                           //Providing Alias Name
    private String empName;
    @Transient
    public String jobTitle;  // It is used for fetching of Designation details when provided with jobTitle in POST and PUT REST API CALL and it is virtually present in table


    @OneToOne                                  // Joining Employee and Designation Table
    @JoinColumn
    @JsonIgnore                               // For Ignoring Designation in JSON output
    Designation designation;
    @Nullable                                // Permitting NUll values in Column
    @JsonIgnore
    private Integer managerId;

    public Employee(String empName, String jobTitle, @Nullable Integer managerId) {
        this.empName = empName;
        this.jobTitle = jobTitle;
        this.managerId = managerId;
    }

    public Employee() {
    }


    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(@Nullable Integer managerId) {
        this.managerId = managerId;
    }

    public String getJobTitle() {
       return getDesignation().getJobTitle();
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

}
