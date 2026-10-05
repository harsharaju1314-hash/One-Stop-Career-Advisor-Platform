package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.AddUserSkillRequest;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.dto.UserRequest;
import com.example.careeradvisor.dto.UserResponse;
import com.example.careeradvisor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<Set<SkillResponse>> getUserSkills(@PathVariable Long id) {
        Set<SkillResponse> skills = userService.getUserSkills(id);
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<UserResponse> addSkillToUser(@PathVariable Long id, @Valid @RequestBody AddUserSkillRequest request) {
        UserResponse response = userService.addSkillToUser(id, request.getSkillId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    public ResponseEntity<UserResponse> removeSkillFromUser(@PathVariable Long id, @PathVariable Long skillId) {
        UserResponse response = userService.removeSkillFromUser(id, skillId);
        return ResponseEntity.ok(response);
    }
}
