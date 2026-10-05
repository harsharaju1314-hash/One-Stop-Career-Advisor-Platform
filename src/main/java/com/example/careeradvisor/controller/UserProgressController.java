package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.ProgressUpdateRequest;
import com.example.careeradvisor.dto.UserProgressResponse;
import com.example.careeradvisor.service.UserProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/progress")
public class UserProgressController {

    private final UserProgressService userProgressService;

    public UserProgressController(UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
    }

    @GetMapping
    public ResponseEntity<List<UserProgressResponse>> getUserProgress(@PathVariable Long userId) {
        List<UserProgressResponse> progressList = userProgressService.getUserProgress(userId);
        return ResponseEntity.ok(progressList);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<UserProgressResponse> updateProgress(
            @PathVariable Long userId,
            @PathVariable Long skillId,
            @Valid @RequestBody ProgressUpdateRequest request) {
        UserProgressResponse response = userProgressService.updateProgress(userId, skillId, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
