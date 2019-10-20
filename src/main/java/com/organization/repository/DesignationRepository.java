package com.organization.repository;

import com.organization.entity.Designation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends CrudRepository<Designation, Integer> {


    public Designation findByJobTitle(String jobTitle); // fetching Designation Details for POST REST API CALL
}