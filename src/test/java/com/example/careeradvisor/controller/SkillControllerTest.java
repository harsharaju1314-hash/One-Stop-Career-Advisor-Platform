package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.SkillRequest;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.service.SkillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillService skillService;

    @Test
    @DisplayName("GET /api/skills - Should return 200 OK with list of skills")
    void testGetAllSkills() throws Exception {
        List<SkillResponse> skills = List.of(
            new SkillResponse(1L, "Java", "Programming"),
            new SkillResponse(2L, "SQL", "Database")
        );

        when(skillService.getAllSkills()).thenReturn(skills);

        mockMvc.perform(get("/api/skills"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    @DisplayName("GET /api/skills/{id} - Should return 200 OK with skill details")
    void testGetSkillById() throws Exception {
        SkillResponse skill = new SkillResponse(1L, "Java", "Programming");
        when(skillService.getSkillById(1L)).thenReturn(skill);

        mockMvc.perform(get("/api/skills/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    @DisplayName("POST /api/skills - Should return 201 Created on valid request")
    void testCreateSkill() throws Exception {
        SkillRequest request = new SkillRequest("Docker", "DevOps");
        SkillResponse response = new SkillResponse(10L, "Docker", "DevOps");

        when(skillService.createSkill(any(SkillRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.name").value("Docker"));
    }

    @Test
    @DisplayName("POST /api/skills - Should return 400 Bad Request on blank name")
    void testCreateSkillValidationFailure() throws Exception {
        SkillRequest request = new SkillRequest("", "DevOps");

        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    @DisplayName("DELETE /api/skills/{id} - Should return 204 No Content on deletion")
    void testDeleteSkill() throws Exception {
        doNothing().when(skillService).deleteSkill(1L);

        mockMvc.perform(delete("/api/skills/1"))
            .andExpect(status().isNoContent());
    }
}
