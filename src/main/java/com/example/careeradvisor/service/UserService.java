package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.CareerRoleSummaryDto;
import com.example.careeradvisor.dto.SkillResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final UserProgressRepository userProgressRepository;

    public UserService(UserRepository userRepository,
                       SkillRepository skillRepository,
                       CareerRoleRepository careerRoleRepository,
                       UserProgressRepository userProgressRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.userProgressRepository = userProgressRepository;
    }

    public UserResponse createUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        CareerRole targetRole = null;
        if (request.getTargetRoleId() != null) {
            targetRole = careerRoleRepository.findById(request.getTargetRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Target career role not found with ID: " + request.getTargetRoleId()));
        }

        User user = new User(
            request.getName().trim(),
            email,
            request.getEducation() != null ? request.getEducation().trim() : null,
            targetRole
        );

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = getUserEntityById(id);
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllWithDetails()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = getUserEntityById(id);
        String newEmail = request.getEmail().trim().toLowerCase();

        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }

        user.setName(request.getName().trim());
        user.setEmail(newEmail);
        user.setEducation(request.getEducation() != null ? request.getEducation().trim() : null);

        if (request.getTargetRoleId() != null) {
            CareerRole targetRole = careerRoleRepository.findById(request.getTargetRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Target career role not found with ID: " + request.getTargetRoleId()));
            user.setTargetRole(targetRole);
        } else {
            user.setTargetRole(null);
        }

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        userProgressRepository.deleteByUserId(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Set<SkillResponse> getUserSkills(Long userId) {
        User user = getUserEntityById(userId);
        return user.getSkills()
            .stream()
            .map(s -> new SkillResponse(s.getId(), s.getName(), s.getCategory()))
            .collect(Collectors.toSet());
    }

    public UserResponse addSkillToUser(Long userId, Long skillId) {
        User user = getUserEntityById(userId);
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        if (user.getSkills().contains(skill)) {
            throw new DuplicateResourceException("User already has skill: " + skill.getName());
        }

        user.addSkill(skill);
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public UserResponse removeSkillFromUser(Long userId, Long skillId) {
        User user = getUserEntityById(userId);
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        if (!user.getSkills().contains(skill)) {
            throw new ResourceNotFoundException("User does not possess skill: " + skill.getName());
        }

        user.removeSkill(skill);
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    public UserResponse mapToResponse(User user) {
        if (user == null) return null;

        CareerRoleSummaryDto targetRoleDto = null;
        if (user.getTargetRole() != null) {
            targetRoleDto = new CareerRoleSummaryDto(user.getTargetRole().getId(), user.getTargetRole().getTitle());
        }

        Set<SkillResponse> skills = user.getSkills()
            .stream()
            .map(s -> new SkillResponse(s.getId(), s.getName(), s.getCategory()))
            .collect(Collectors.toSet());

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getEducation(),
            targetRoleDto,
            skills
        );
    }
}
