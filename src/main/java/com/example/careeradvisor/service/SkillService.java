package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.SkillRequest;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAllByOrderByNameAsc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Skill getSkillEntityById(Long id) {
        return skillRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        return mapToResponse(getSkillEntityById(id));
    }

    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Skill already exists with name: " + request.getName());
        }

        Skill skill = new Skill(request.getName().trim(), request.getCategory().trim());
        Skill saved = skillRepository.save(skill);
        return mapToResponse(saved);
    }

    public void deleteSkill(Long id) {
        Skill skill = getSkillEntityById(id);
        skillRepository.delete(skill);
    }

    public SkillResponse mapToResponse(Skill skill) {
        if (skill == null) return null;
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());
    }
}
