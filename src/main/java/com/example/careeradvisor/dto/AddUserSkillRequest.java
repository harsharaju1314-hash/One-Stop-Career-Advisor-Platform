package com.example.careeradvisor.dto;

import jakarta.validation.constraints.NotNull;

public class AddUserSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    public AddUserSkillRequest() {
    }

    public AddUserSkillRequest(Long skillId) {
        this.skillId = skillId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
