package com.example.careeradvisor.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String education;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_role_id")
    private CareerRole targetRole;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_skills",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    public User() {
    }

    public User(String name, String email, String education, CareerRole targetRole) {
        this.name = name;
        this.email = email;
        this.education = education;
        this.targetRole = targetRole;
    }

    public User(Long id, String name, String email, String education, CareerRole targetRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.education = education;
        this.targetRole = targetRole;
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

    public CareerRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(CareerRole targetRole) {
        this.targetRole = targetRole;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
