package com.example.careeradvisor.dto;

import java.util.HashSet;
import java.util.Set;

public class CareerRoleResponse {

    private Long id;
    private String title;
    private String description;
    private Set<SkillResponse> requiredSkills = new HashSet<>();

    public CareerRoleResponse() {
    }

    public CareerRoleResponse(Long id, String title, String description, Set<SkillResponse> requiredSkills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SkillResponse> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<SkillResponse> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
