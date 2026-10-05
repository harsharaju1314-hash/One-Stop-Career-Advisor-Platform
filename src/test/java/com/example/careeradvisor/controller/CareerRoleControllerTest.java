package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.CareerRoleResponse;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.service.CareerRoleService;
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

@WebMvcTest(CareerRoleController.class)
class CareerRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CareerRoleService careerRoleService;

    @Test
    @DisplayName("GET /api/roles - Should return list of all career roles")
    void testGetAllRoles() throws Exception {
        CareerRoleResponse role = new CareerRoleResponse(
            1L, "Java Backend Developer", "Develop backend services",
            Set.of(new SkillResponse(1L, "Java", "Programming"))
        );

        when(careerRoleService.getAllRoles()).thenReturn(List.of(role));

        mockMvc.perform(get("/api/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Java Backend Developer"))
            .andExpect(jsonPath("$[0].requiredSkills[0].name").value("Java"));
    }

    @Test
    @DisplayName("GET /api/roles/{id} - Should return 404 when role not found")
    void testGetRoleByIdNotFound() throws Exception {
        when(careerRoleService.getRoleById(999L))
            .thenThrow(new ResourceNotFoundException("Career role not found with ID: 999"));

        mockMvc.perform(get("/api/roles/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
