package com.example.careeradvisor.dto;

import java.util.HashSet;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String education;
    private CareerRoleSummaryDto targetRole;
    private Set<SkillResponse> skills = new HashSet<>();

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, String education, CareerRoleSummaryDto targetRole, Set<SkillResponse> skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.education = education;
        this.targetRole = targetRole;
        this.skills = skills;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CareerRoleSummaryDto getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(CareerRoleSummaryDto targetRole) {
        this.targetRole = targetRole;
    }

    public Set<SkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillResponse> skills) {
        this.skills = skills;
    }
}
