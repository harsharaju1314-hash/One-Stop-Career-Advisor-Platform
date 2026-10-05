package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.AddUserSkillRequest;
import com.example.careeradvisor.dto.CareerRoleSummaryDto;
import com.example.careeradvisor.dto.UserRequest;
import com.example.careeradvisor.dto.UserResponse;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - Should return 201 Created on valid request")
    void testCreateUserSuccess() throws Exception {
        UserRequest request = new UserRequest("John Doe", "john@example.com", "B.Tech", 1L);
        UserResponse response = new UserResponse(
            1L, "John Doe", "john@example.com", "B.Tech",
            new CareerRoleSummaryDto(1L, "Java Backend Developer"),
            new HashSet<>()
        );

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 Bad Request on invalid email")
    void testCreateUserInvalidEmail() throws Exception {
        UserRequest request = new UserRequest("John Doe", "invalid-email", "B.Tech", 1L);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 200 OK with user details")
    void testGetUserByIdSuccess() throws Exception {
        UserResponse response = new UserResponse(
            1L, "John Doe", "john@example.com", "B.Tech", null, new HashSet<>()
        );

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 Not Found when user does not exist")
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("User not found with ID: 999"));
    }

    @Test
    @DisplayName("POST /api/users/{id}/skills - Should return 200 OK when skill added")
    void testAddSkillToUserSuccess() throws Exception {
        AddUserSkillRequest request = new AddUserSkillRequest(5L);
        UserResponse response = new UserResponse(
            1L, "John Doe", "john@example.com", "B.Tech", null, new HashSet<>()
        );

        when(userService.addSkillToUser(eq(1L), eq(5L))).thenReturn(response);

        mockMvc.perform(post("/api/users/1/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/users/{id}/skills - Should return 409 Conflict on duplicate skill")
    void testAddDuplicateSkillToUser() throws Exception {
        AddUserSkillRequest request = new AddUserSkillRequest(5L);
        when(userService.addSkillToUser(eq(1L), eq(5L)))
            .thenThrow(new DuplicateResourceException("User already has skill: Java"));

        mockMvc.perform(post("/api/users/1/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }
}
