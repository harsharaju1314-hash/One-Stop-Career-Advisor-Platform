package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.ProgressUpdateRequest;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.dto.UserProgressResponse;
import com.example.careeradvisor.entity.ProgressStatus;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.service.UserProgressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProgressController.class)
class UserProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserProgressService userProgressService;

    @Test
    @DisplayName("GET /api/users/{id}/progress - Should return list of user progress")
    void testGetUserProgress() throws Exception {
        UserProgressResponse progress = new UserProgressResponse(
            1L, 100L, new SkillResponse(5L, "Spring Boot", "Framework"),
            ProgressStatus.IN_PROGRESS, LocalDateTime.now()
        );

        when(userProgressService.getUserProgress(100L)).thenReturn(List.of(progress));

        mockMvc.perform(get("/api/users/100/progress"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$[0].skill.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("PUT /api/users/{id}/progress/{skillId} - Should update progress status")
    void testUpdateProgress() throws Exception {
        ProgressUpdateRequest request = new ProgressUpdateRequest(ProgressStatus.COMPLETED);
        UserProgressResponse response = new UserProgressResponse(
            1L, 100L, new SkillResponse(5L, "Spring Boot", "Framework"),
            ProgressStatus.COMPLETED, LocalDateTime.now()
        );

        when(userProgressService.updateProgress(eq(100L), eq(5L), eq(ProgressStatus.COMPLETED)))
            .thenReturn(response);

        mockMvc.perform(put("/api/users/100/progress/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
