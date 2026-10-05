package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.CareerAdviceResponse;
import com.example.careeradvisor.dto.MissingSkillAdviceDto;
import com.example.careeradvisor.entity.*;
import com.example.careeradvisor.exception.BadRequestException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.CareerRoleRepository;
import com.example.careeradvisor.repository.RecommendationRepository;
import com.example.careeradvisor.repository.UserProgressRepository;
import com.example.careeradvisor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerAdviceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRoleRepository careerRoleRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @InjectMocks
    private CareerAdviceService careerAdviceService;

    private Skill javaSkill;
    private Skill sqlSkill;
    private Skill gitSkill;
    private Skill springBootSkill;
    private Skill restApiSkill;
    private CareerRole javaDevRole;
    private User user;

    @BeforeEach
    void setUp() {
        javaSkill = new Skill(1L, "Java", "Programming");
        sqlSkill = new Skill(2L, "SQL", "Database");
        gitSkill = new Skill(3L, "Git", "Tools");
        springBootSkill = new Skill(4L, "Spring Boot", "Framework");
        restApiSkill = new Skill(5L, "REST API", "Architecture");

        javaDevRole = new CareerRole(10L, "Java Backend Developer", "Backend development");
        javaDevRole.setRequiredSkills(new HashSet<>(Set.of(
            javaSkill, sqlSkill, gitSkill, springBootSkill, restApiSkill
        )));

        user = new User(100L, "Alice Johnson", "alice@example.com", "B.Tech CS", javaDevRole);
    }

    @Test
    @DisplayName("Should correctly calculate partial match, skill gaps and match percentage")
    void testPartialSkillMatch() {
        // User has Java, SQL, Git (3 out of 5 required skills)
        user.setSkills(new HashSet<>(Set.of(javaSkill, sqlSkill, gitSkill)));

        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(careerRoleRepository.findByIdWithSkills(10L)).thenReturn(Optional.of(javaDevRole));
        when(recommendationRepository.findBestMatchingRecommendations(eq(10L), anyLong()))
            .thenReturn(List.of(new Recommendation(springBootSkill, "Learn Spring Boot", "Baeldung")));
        when(userProgressRepository.findByUserIdAndSkillId(eq(100L), anyLong()))
            .thenReturn(Optional.empty());

        CareerAdviceResponse response = careerAdviceService.generateCareerAdvice(100L);

        assertNotNull(response);
        assertEquals(100L, response.getUserId());
        assertEquals("Alice Johnson", response.getUserName());
        assertEquals("Java Backend Developer", response.getTargetRole());
        assertEquals(5, response.getTotalRequiredSkills());
        assertEquals(3, response.getMatchedSkillsCount());
        assertEquals(2, response.getMissingSkillsCount());
        assertEquals(60.0, response.getMatchPercentage()); // (3/5) * 100 = 60.0%

        assertEquals(2, response.getSkillGaps().size());
        List<String> missingSkillNames = response.getSkillGaps().stream()
            .map(MissingSkillAdviceDto::getSkillName)
            .toList();
        assertTrue(missingSkillNames.contains("Spring Boot"));
        assertTrue(missingSkillNames.contains("REST API"));
        assertFalse(missingSkillNames.contains("Java"));
    }

    @Test
    @DisplayName("Should return 100% match when user possesses all required skills")
    void testFullSkillMatch() {
        // User has all 5 skills
        user.setSkills(new HashSet<>(Set.of(javaSkill, sqlSkill, gitSkill, springBootSkill, restApiSkill)));

        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(careerRoleRepository.findByIdWithSkills(10L)).thenReturn(Optional.of(javaDevRole));

        CareerAdviceResponse response = careerAdviceService.generateCareerAdvice(100L);

        assertNotNull(response);
        assertEquals(5, response.getMatchedSkillsCount());
        assertEquals(0, response.getMissingSkillsCount());
        assertEquals(100.0, response.getMatchPercentage());
        assertTrue(response.getSkillGaps().isEmpty());
    }

    @Test
    @DisplayName("Should return 0% match when user possesses none of the required skills")
    void testZeroSkillMatch() {
        // User has no skills
        user.setSkills(new HashSet<>());

        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));
        when(careerRoleRepository.findByIdWithSkills(10L)).thenReturn(Optional.of(javaDevRole));
        when(recommendationRepository.findBestMatchingRecommendations(anyLong(), anyLong()))
            .thenReturn(Collections.emptyList());
        when(userProgressRepository.findByUserIdAndSkillId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        CareerAdviceResponse response = careerAdviceService.generateCareerAdvice(100L);

        assertNotNull(response);
        assertEquals(0, response.getMatchedSkillsCount());
        assertEquals(5, response.getMissingSkillsCount());
        assertEquals(0.0, response.getMatchPercentage());
        assertEquals(5, response.getSkillGaps().size());
    }

    @Test
    @DisplayName("Should throw BadRequestException if user has not selected a target career role")
    void testThrowsExceptionWhenTargetRoleNull() {
        user.setTargetRole(null);
        when(userRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(user));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            careerAdviceService.generateCareerAdvice(100L)
        );

        assertTrue(ex.getMessage().contains("has not selected a target career role"));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
    void testThrowsExceptionWhenUserNotFound() {
        when(userRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            careerAdviceService.generateCareerAdvice(999L)
        );

        assertTrue(ex.getMessage().contains("User not found with ID: 999"));
    }
}
