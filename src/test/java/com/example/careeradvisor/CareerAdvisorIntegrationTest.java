package com.example.careeradvisor;

import com.example.careeradvisor.dto.AddUserSkillRequest;
import com.example.careeradvisor.dto.ProgressUpdateRequest;
import com.example.careeradvisor.dto.UserRequest;
import com.example.careeradvisor.entity.CareerRole;
import com.example.careeradvisor.entity.ProgressStatus;
import com.example.careeradvisor.entity.Recommendation;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.repository.CareerRoleRepository;
import com.example.careeradvisor.repository.RecommendationRepository;
import com.example.careeradvisor.repository.SkillRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CareerAdvisorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CareerRoleRepository careerRoleRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    private Skill javaSkill;
    private Skill springBootSkill;
    private Skill sqlSkill;
    private CareerRole backendRole;

    @BeforeEach
    void setupTestData() {
        javaSkill = skillRepository.save(new Skill("Java", "Programming"));
        springBootSkill = skillRepository.save(new Skill("Spring Boot", "Framework"));
        sqlSkill = skillRepository.save(new Skill("SQL", "Database"));

        backendRole = new CareerRole("Java Developer", "Backend software engineering");
        backendRole.setRequiredSkills(new HashSet<>(Set.of(javaSkill, springBootSkill, sqlSkill)));
        backendRole = careerRoleRepository.save(backendRole);

        recommendationRepository.save(new Recommendation(
            springBootSkill,
            "Build a REST API using Spring Boot",
            "Spring.io Guides"
        ));
    }

    @Test
    @DisplayName("End-to-End Flow: Create user -> Add skill -> Get career advice -> Update progress -> Check updated advice")
    void testEndToEndCareerAdviceWorkflow() throws Exception {
        // 1. Create User
        UserRequest userRequest = new UserRequest("E2E Candidate", "e2e@example.com", "Computer Science", backendRole.getId());
        MvcResult userResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("E2E Candidate"))
            .andReturn();

        String responseBody = userResult.getResponse().getContentAsString();
        Long userId = objectMapper.readTree(responseBody).get("id").asLong();

        // 2. Add 'Java' skill to user
        AddUserSkillRequest skillRequest = new AddUserSkillRequest(javaSkill.getId());
        mockMvc.perform(post("/api/users/" + userId + "/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.skills[0].name").value("Java"));

        // 3. Request Career Advice: 1/3 matched (33.3%), 2 missing (Spring Boot, SQL)
        mockMvc.perform(get("/api/career-advice/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRequiredSkills").value(3))
            .andExpect(jsonPath("$.matchedSkillsCount").value(1))
            .andExpect(jsonPath("$.missingSkillsCount").value(2))
            .andExpect(jsonPath("$.matchPercentage").value(33.3))
            .andExpect(jsonPath("$.skillGaps").isArray());

        // 4. Update progress for 'Spring Boot' to IN_PROGRESS
        ProgressUpdateRequest inProgressRequest = new ProgressUpdateRequest(ProgressStatus.IN_PROGRESS);
        mockMvc.perform(put("/api/users/" + userId + "/progress/" + springBootSkill.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inProgressRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 5. Update progress for 'Spring Boot' to COMPLETED (should automatically add to user skills)
        ProgressUpdateRequest completedRequest = new ProgressUpdateRequest(ProgressStatus.COMPLETED);
        mockMvc.perform(put("/api/users/" + userId + "/progress/" + springBootSkill.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completedRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 6. Verify Career Advice now has 2/3 matched (66.7%) and only 1 missing (SQL)
        mockMvc.perform(get("/api/career-advice/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matchedSkillsCount").value(2))
            .andExpect(jsonPath("$.missingSkillsCount").value(1))
            .andExpect(jsonPath("$.matchPercentage").value(66.7));
    }
}
