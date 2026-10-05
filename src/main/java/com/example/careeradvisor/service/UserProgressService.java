package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.dto.UserProgressResponse;
import com.example.careeradvisor.entity.ProgressStatus;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.entity.User;
import com.example.careeradvisor.entity.UserProgress;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.SkillRepository;
import com.example.careeradvisor.repository.UserProgressRepository;
import com.example.careeradvisor.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserProgressService(UserProgressRepository userProgressRepository,
                               UserRepository userRepository,
                               SkillRepository skillRepository) {
        this.userProgressRepository = userProgressRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<UserProgressResponse> getUserProgress(Long userId) {
        // Validate user existence
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        return userProgressRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public UserProgressResponse updateProgress(Long userId, Long skillId, ProgressStatus status) {
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        UserProgress progress = userProgressRepository.findByUserIdAndSkillId(userId, skillId)
            .orElseGet(() -> new UserProgress(user, skill, status));

        progress.setStatus(status);
        progress.setUpdatedAt(LocalDateTime.now());

        // When a user completes a recommended skill, automatically add it to user's acquired skills
        if (status == ProgressStatus.COMPLETED && !user.getSkills().contains(skill)) {
            user.addSkill(skill);
            userRepository.save(user);
        }

        UserProgress saved = userProgressRepository.save(progress);
        return mapToResponse(saved);
    }

    public UserProgressResponse mapToResponse(UserProgress progress) {
        if (progress == null) return null;
        Skill skill = progress.getSkill();
        SkillResponse skillDto = new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());

        return new UserProgressResponse(
            progress.getId(),
            progress.getUser().getId(),
            skillDto,
            progress.getStatus(),
            progress.getUpdatedAt()
        );
    }
}
