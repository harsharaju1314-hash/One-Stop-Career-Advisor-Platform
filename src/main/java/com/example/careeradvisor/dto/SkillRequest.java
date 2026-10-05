package com.example.careeradvisor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillRequest {

    @NotBlank(message = "Skill name is required and cannot be blank")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Category is required and cannot be blank")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    public SkillRequest() {
    }

    public SkillRequest(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
