package com.example.careeradvisor.dto;

import com.example.careeradvisor.entity.ProgressStatus;
import java.time.LocalDateTime;

public class UserProgressResponse {

    private Long id;
    private Long userId;
    private SkillResponse skill;
    private ProgressStatus status;
    private LocalDateTime updatedAt;

    public UserProgressResponse() {
    }

    public UserProgressResponse(Long id, Long userId, SkillResponse skill, ProgressStatus status, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.skill = skill;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SkillResponse getSkill() {
        return skill;
    }

    public void setSkill(SkillResponse skill) {
        this.skill = skill;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
