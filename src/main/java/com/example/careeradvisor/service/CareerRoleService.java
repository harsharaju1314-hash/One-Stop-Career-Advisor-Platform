package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.CareerRoleResponse;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.entity.CareerRole;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.exception.DuplicateResourceException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.CareerRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareerRoleService {

    private final CareerRoleRepository careerRoleRepository;

    public CareerRoleService(CareerRoleRepository careerRoleRepository) {
        this.careerRoleRepository = careerRoleRepository;
    }

    @Transactional(readOnly = true)
    public List<CareerRoleResponse> getAllRoles() {
        return careerRoleRepository.findAllWithSkills()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareerRole getRoleEntityById(Long id) {
        return careerRoleRepository.findByIdWithSkills(id)
            .orElseThrow(() -> new ResourceNotFoundException("Career role not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public CareerRoleResponse getRoleById(Long id) {
        return mapToResponse(getRoleEntityById(id));
    }

    public CareerRole createRole(String title, String description, Set<Skill> requiredSkills) {
        if (careerRoleRepository.existsByTitleIgnoreCase(title.trim())) {
            throw new DuplicateResourceException("Career role already exists with title: " + title);
        }

        CareerRole role = new CareerRole(title.trim(), description != null ? description.trim() : "");
        if (requiredSkills != null) {
            role.setRequiredSkills(requiredSkills);
        }
        return careerRoleRepository.save(role);
    }

    public CareerRoleResponse mapToResponse(CareerRole role) {
        if (role == null) return null;
        Set<SkillResponse> skills = role.getRequiredSkills()
            .stream()
            .map(s -> new SkillResponse(s.getId(), s.getName(), s.getCategory()))
            .collect(Collectors.toSet());
        return new CareerRoleResponse(role.getId(), role.getTitle(), role.getDescription(), skills);
    }
}
