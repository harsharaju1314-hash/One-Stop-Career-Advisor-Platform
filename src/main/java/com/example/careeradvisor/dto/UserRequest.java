package com.example.careeradvisor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank(message = "Name is required and cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 200, message = "Education details must not exceed 200 characters")
    private String education;

    private Long targetRoleId;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String education, Long targetRoleId) {
        this.name = name;
        this.email = email;
        this.education = education;
        this.targetRoleId = targetRoleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Long getTargetRoleId() {
        return targetRoleId;
    }

    public void setTargetRoleId(Long targetRoleId) {
        this.targetRoleId = targetRoleId;
    }
}
