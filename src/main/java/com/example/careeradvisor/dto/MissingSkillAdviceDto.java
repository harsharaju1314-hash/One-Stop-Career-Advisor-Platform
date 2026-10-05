package com.example.careeradvisor.dto;

import com.example.careeradvisor.entity.ProgressStatus;

public class MissingSkillAdviceDto {

    private Long skillId;
    private String skillName;
    private String category;
    private String recommendation;
    private String learningResource;
    private ProgressStatus currentStatus;

    public MissingSkillAdviceDto() {
    }

    public MissingSkillAdviceDto(Long skillId, String skillName, String category, String recommendation, String learningResource, ProgressStatus currentStatus) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.recommendation = recommendation;
        this.learningResource = learningResource;
        this.currentStatus = currentStatus;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getLearningResource() {
        return learningResource;
    }

    public void setLearningResource(String learningResource) {
        this.learningResource = learningResource;
    }

    public ProgressStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ProgressStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
}
