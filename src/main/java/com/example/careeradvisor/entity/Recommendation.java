package com.example.careeradvisor.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private CareerRole careerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false, length = 1000)
    private String advice;

    @Column(length = 500)
    private String learningResource;

    public Recommendation() {
    }

    public Recommendation(Skill skill, String advice, String learningResource) {
        this.skill = skill;
        this.advice = advice;
        this.learningResource = learningResource;
    }

    public Recommendation(CareerRole careerRole, Skill skill, String advice, String learningResource) {
        this.careerRole = careerRole;
        this.skill = skill;
        this.advice = advice;
        this.learningResource = learningResource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerRole getCareerRole() {
        return careerRole;
    }

    public void setCareerRole(CareerRole careerRole) {
        this.careerRole = careerRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getLearningResource() {
        return learningResource;
    }

    public void setLearningResource(String learningResource) {
        this.learningResource = learningResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
