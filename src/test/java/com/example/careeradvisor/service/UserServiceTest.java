package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.UserRequest;
import com.example.careeradvisor.dto.UserResponse;
import com.example.careeradvisor.entity.CareerRole;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.entity.User;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.CareerRoleRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private CareerRoleRepository careerRoleRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @InjectMocks
    private UserService userService;

    private CareerRole role;
    private Skill javaSkill;
    private User user;

    @BeforeEach
    void setUp() {
        role = new CareerRole(1L, "Java Backend Developer", "Backend");
        javaSkill = new Skill(10L, "Java", "Programming Language");
        user = new User(100L, "Bob Smith", "bob@example.com", "B.Sc CS", role);
        user.setSkills(new HashSet<>());
    }

    @Test
    @DisplayName("Should successfully create a new user")
    void testCreateUserSuccess() {
        UserRequest request = new UserRequest("Bob Smith", "bob@example.com", "B.Sc CS", 1L);

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(careerRoleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("Bob Smith", response.getName());
        assertEquals("bob@example.com", response.getEmail());
        assertEquals("Java Backend Developer", response.getTargetRole().getTitle());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void testCreateUserDuplicateEmail() {
        UserRequest request = new UserRequest("Bob Smith", "bob@example.com", "B.Sc CS", 1L);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully add a skill to a user")
    void testAddSkillToUser() {
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(javaSkill));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.addSkillToUser(100L, 10L);

        assertNotNull(response);
        assertTrue(user.getSkills().contains(javaSkill));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when adding already possessed skill")
    void testAddDuplicateSkillToUser() {
        user.addSkill(javaSkill);
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(javaSkill));

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () ->
            userService.addSkillToUser(100L, 10L)
        );

        assertTrue(ex.getMessage().contains("User already has skill"));
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Should successfully remove a skill from a user")
    void testRemoveSkillFromUser() {
        user.addSkill(javaSkill);
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(javaSkill));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.removeSkillFromUser(100L, 10L);

        assertNotNull(response);
        assertFalse(user.getSkills().contains(javaSkill));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing skill user does not possess")
    void testRemoveNonExistentSkillFromUser() {
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(javaSkill));

        assertThrows(ResourceNotFoundException.class, () ->
            userService.removeSkillFromUser(100L, 10L)
        );
    }
}
