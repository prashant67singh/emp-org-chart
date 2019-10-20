package com.organization.repository;

import com.organization.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    public List<Employee> findAllByOrderByDesignation_LevelIdAscEmpNameAsc(); // Declaration of Abstract method for finding details of all employee

}
