package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.UserProgressResponse;
import com.example.careeradvisor.entity.ProgressStatus;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.entity.User;
import com.example.careeradvisor.entity.UserProgress;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.SkillRepository;
import com.example.careeradvisor.repository.UserProgressRepository;
import com.example.careeradvisor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProgressServiceTest {

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private UserProgressService userProgressService;

    private User user;
    private Skill springBootSkill;

    @BeforeEach
    void setUp() {
        user = new User(100L, "Charlie", "charlie@example.com", "Degree", null);
        user.setSkills(new HashSet<>());
        springBootSkill = new Skill(5L, "Spring Boot", "Framework");
    }

    @Test
    @DisplayName("Should create and update user progress status to IN_PROGRESS")
    void testUpdateProgressToInProgress() {
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(5L)).thenReturn(Optional.of(springBootSkill));
        when(userProgressRepository.findByUserIdAndSkillId(100L, 5L)).thenReturn(Optional.empty());

        UserProgress savedProgress = new UserProgress(user, springBootSkill, ProgressStatus.IN_PROGRESS);
        savedProgress.setId(1L);
        when(userProgressRepository.save(any(UserProgress.class))).thenReturn(savedProgress);

        UserProgressResponse response = userProgressService.updateProgress(100L, 5L, ProgressStatus.IN_PROGRESS);

        assertNotNull(response);
        assertEquals(ProgressStatus.IN_PROGRESS, response.getStatus());
        assertEquals("Spring Boot", response.getSkill().getName());
        verify(userProgressRepository, times(1)).save(any(UserProgress.class));
    }

    @Test
    @DisplayName("Should automatically add skill to user skills when status is set to COMPLETED")
    void testUpdateProgressToCompletedAddsSkillToUser() {
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(5L)).thenReturn(Optional.of(springBootSkill));
        when(userProgressRepository.findByUserIdAndSkillId(100L, 5L)).thenReturn(Optional.empty());

        UserProgress savedProgress = new UserProgress(user, springBootSkill, ProgressStatus.COMPLETED);
        savedProgress.setId(1L);
        when(userProgressRepository.save(any(UserProgress.class))).thenReturn(savedProgress);

        UserProgressResponse response = userProgressService.updateProgress(100L, 5L, ProgressStatus.COMPLETED);

        assertNotNull(response);
        assertEquals(ProgressStatus.COMPLETED, response.getStatus());
        assertTrue(user.getSkills().contains(springBootSkill));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user or skill not found")
    void testUpdateProgressNotFound() {
        when(userRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userProgressService.updateProgress(999L, 5L, ProgressStatus.IN_PROGRESS)
        );
    }
}
