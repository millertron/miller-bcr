package com.millertronics.millerapp.millerbcr;

import android.text.Editable;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Koha Choji on 06/06/2017.
 */

public class Profile {
    private Integer id;
    private String name;
    private String jobTitle;
    private String company;
    private String primaryContactNumber;
    private String secondaryContactNumber;
    private String email;

    public Profile(String name,
                   String jobTitle,
                   String company,
                   String primaryContactNumber,
                   String email) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.company = company;
        this.primaryContactNumber = primaryContactNumber;
        this.email = email;
    }

    public boolean isValid(){
        boolean isValid = StringUtils.isNotBlank(name)
                && StringUtils.isNotBlank(company);
        return isValid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public String getSecondaryContactNumber() {
        return secondaryContactNumber;
    }

    public void setSecondaryContactNumber(String secondaryContactNumber) {
        this.secondaryContactNumber = secondaryContactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

}
