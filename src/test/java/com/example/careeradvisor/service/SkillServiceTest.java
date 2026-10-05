package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.SkillRequest;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.SkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    @DisplayName("Should create skill successfully when name is unique")
    void testCreateSkillSuccess() {
        SkillRequest request = new SkillRequest("Java", "Programming");
        Skill skill = new Skill(1L, "Java", "Programming");

        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        SkillResponse response = skillService.createSkill(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java", response.getName());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on duplicate skill name")
    void testCreateDuplicateSkill() {
        SkillRequest request = new SkillRequest("Java", "Programming");
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> skillService.createSkill(request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when skill ID does not exist")
    void testGetSkillByIdNotFound() {
        when(skillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> skillService.getSkillById(999L));
    }
}
