package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.CareerAdviceResponse;
import com.example.careeradvisor.dto.MissingSkillAdviceDto;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.entity.ProgressStatus;
import com.example.careeradvisor.exception.BadRequestException;
import com.example.careeradvisor.service.CareerAdviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CareerAdviceController.class)
class CareerAdviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CareerAdviceService careerAdviceService;

    @Test
    @DisplayName("GET /api/career-advice/{userId} - Should return 200 OK with advice payload")
    void testGetCareerAdviceSuccess() throws Exception {
        CareerAdviceResponse response = new CareerAdviceResponse(
            1L,
            "Alice",
            "Java Backend Developer",
            4,
            2,
            2,
            50.0,
            Set.of(new SkillResponse(1L, "Java", "Programming"), new SkillResponse(2L, "SQL", "Database")),
            List.of(new MissingSkillAdviceDto(3L, "Spring Boot", "Framework", "Learn Spring Boot", "Baeldung", ProgressStatus.NOT_STARTED)),
            "Rule-based match"
        );

        when(careerAdviceService.generateCareerAdvice(1L)).thenReturn(response);

        mockMvc.perform(get("/api/career-advice/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.userName").value("Alice"))
            .andExpect(jsonPath("$.targetRole").value("Java Backend Developer"))
            .andExpect(jsonPath("$.totalRequiredSkills").value(4))
            .andExpect(jsonPath("$.matchedSkillsCount").value(2))
            .andExpect(jsonPath("$.missingSkillsCount").value(2))
            .andExpect(jsonPath("$.matchPercentage").value(50.0))
            .andExpect(jsonPath("$.skillGaps[0].skillName").value("Spring Boot"));
    }

    @Test
    @DisplayName("GET /api/career-advice/{userId} - Should return 400 Bad Request if user has no target role")
    void testGetCareerAdviceNoTargetRole() throws Exception {
        when(careerAdviceService.generateCareerAdvice(1L))
            .thenThrow(new BadRequestException("User has not selected a target career role."));

        mockMvc.perform(get("/api/career-advice/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("User has not selected a target career role."));
    }
}
