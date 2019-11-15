package com.organization.controller;

import com.organization.entity.EmployeePost;
import com.organization.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class  EmployeeController {

    private EmployeeService employeeService;
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

        @GetMapping("/employees")
        public ResponseEntity getAllEmployee(){
            return new ResponseEntity<>(employeeService.getAllEmployee(), HttpStatus.OK);
         }

        @GetMapping("/employees/{id}")
        public ResponseEntity getEmployee(@PathVariable("id") int id){
            return new ResponseEntity<>(employeeService.getEmployee(id),HttpStatus.OK);
        }

        @DeleteMapping("/employees/{id}")
         public ResponseEntity deleteEmployeeById(@PathVariable("id")int id){
            return new ResponseEntity<>(employeeService.deleteEmployeeById(id),HttpStatus.NO_CONTENT);
        }

        @PostMapping("/employees")
         public ResponseEntity addEmployee(@RequestBody EmployeePost employee){
            return new ResponseEntity<>(employeeService.addEmployee(employee),HttpStatus.CREATED);
        }

        @PutMapping("/employees/{id}")
        public ResponseEntity updateEmployeeDetails(@PathVariable("id")int id,@RequestBody EmployeePost employee){
            return  new ResponseEntity<>(employeeService.updateEmployeeDetails(id, employee),HttpStatus.OK);
        }
}
