package com.organization.service;

import com.organization.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DesignationService {


    private DesignationRepository designationRepository;
    @Autowired
    public DesignationService(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }


}
