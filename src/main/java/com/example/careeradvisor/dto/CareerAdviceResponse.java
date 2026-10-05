package com.example.careeradvisor.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CareerAdviceResponse {

    private Long userId;
    private String userName;
    private String targetRole;
    private int totalRequiredSkills;
    private int matchedSkillsCount;
    private int missingSkillsCount;
    private double matchPercentage;
    private Set<SkillResponse> matchedSkills = new HashSet<>();
    private List<MissingSkillAdviceDto> skillGaps = new ArrayList<>();
    private String note;

    public CareerAdviceResponse() {
    }

    public CareerAdviceResponse(Long userId, String userName, String targetRole, int totalRequiredSkills,
                                int matchedSkillsCount, int missingSkillsCount, double matchPercentage,
                                Set<SkillResponse> matchedSkills, List<MissingSkillAdviceDto> skillGaps,
                                String note) {
        this.userId = userId;
        this.userName = userName;
        this.targetRole = targetRole;
        this.totalRequiredSkills = totalRequiredSkills;
        this.matchedSkillsCount = matchedSkillsCount;
        this.missingSkillsCount = missingSkillsCount;
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.skillGaps = skillGaps;
        this.note = note;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public int getTotalRequiredSkills() {
        return totalRequiredSkills;
    }

    public void setTotalRequiredSkills(int totalRequiredSkills) {
        this.totalRequiredSkills = totalRequiredSkills;
    }

    public int getMatchedSkillsCount() {
        return matchedSkillsCount;
    }

    public void setMatchedSkillsCount(int matchedSkillsCount) {
        this.matchedSkillsCount = matchedSkillsCount;
    }

    public int getMissingSkillsCount() {
        return missingSkillsCount;
    }

    public void setMissingSkillsCount(int missingSkillsCount) {
        this.missingSkillsCount = missingSkillsCount;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public Set<SkillResponse> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(Set<SkillResponse> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<MissingSkillAdviceDto> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<MissingSkillAdviceDto> skillGaps) {
        this.skillGaps = skillGaps;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
