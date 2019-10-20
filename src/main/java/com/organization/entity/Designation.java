package com.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import javax.persistence.*;

@Entity
@JsonIgnoreType
public class Designation  {

    @Id                                               // Declaring Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // For Generating Id Automatically
    @JsonIgnore
    private Integer designationId;
    @JsonIgnore
    private String jobTitle;
    @JsonIgnore
    private Integer levelId;




    public Integer getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Integer designationId) {
        this.designationId = designationId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    }
