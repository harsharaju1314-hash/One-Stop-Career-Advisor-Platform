package com.example.careeradvisor.service;

import com.example.careeradvisor.dto.CareerAdviceResponse;
import com.example.careeradvisor.dto.MissingSkillAdviceDto;
import com.example.careeradvisor.dto.SkillResponse;
import com.example.careeradvisor.entity.*;
import com.example.careeradvisor.exception.BadRequestException;
import com.example.careeradvisor.exception.ResourceNotFoundException;
import com.example.careeradvisor.repository.CareerRoleRepository;
import com.example.careeradvisor.repository.RecommendationRepository;
import com.example.careeradvisor.repository.UserProgressRepository;
import com.example.careeradvisor.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CareerAdviceService {

    private final UserRepository userRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserProgressRepository userProgressRepository;

    public CareerAdviceService(UserRepository userRepository,
                               CareerRoleRepository careerRoleRepository,
                               RecommendationRepository recommendationRepository,
                               UserProgressRepository userProgressRepository) {
        this.userRepository = userRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.recommendationRepository = recommendationRepository;
        this.userProgressRepository = userProgressRepository;
    }

    public CareerAdviceResponse generateCareerAdvice(Long userId) {
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        CareerRole targetRole = user.getTargetRole();
        if (targetRole == null) {
            throw new BadRequestException("User '" + user.getName() + "' has not selected a target career role. Please update user profile with a target role.");
        }

        // Fetch target role with fully initialized required skills
        CareerRole roleWithSkills = careerRoleRepository.findByIdWithSkills(targetRole.getId())
            .orElse(targetRole);

        Set<Skill> userSkills = user.getSkills() != null ? user.getSkills() : Collections.emptySet();
        Set<Skill> requiredSkills = roleWithSkills.getRequiredSkills() != null ? roleWithSkills.getRequiredSkills() : Collections.emptySet();

        // 1. Identify matched skills (intersection)
        Set<Skill> matchedSkillsSet = requiredSkills.stream()
            .filter(userSkills::contains)
            .collect(Collectors.toSet());

        // 2. Identify skill gaps / missing skills (difference)
        List<Skill> missingSkillsList = requiredSkills.stream()
            .filter(req -> !userSkills.contains(req))
            .sorted(Comparator.comparing(Skill::getName))
            .collect(Collectors.toList());

        int totalRequired = requiredSkills.size();
        int matchedCount = matchedSkillsSet.size();
        int missingCount = missingSkillsList.size();

        double matchPercentage = 0.0;
        if (totalRequired > 0) {
            double calculated = ((double) matchedCount / totalRequired) * 100.0;
            matchPercentage = Math.round(calculated * 10.0) / 10.0; // 1 decimal place
        }

        Set<SkillResponse> matchedSkillsDto = matchedSkillsSet.stream()
            .map(s -> new SkillResponse(s.getId(), s.getName(), s.getCategory()))
            .collect(Collectors.toSet());

        // 3. Build learning recommendations & progress status for missing skills
        List<MissingSkillAdviceDto> skillGaps = new ArrayList<>();
        for (Skill missingSkill : missingSkillsList) {
            List<Recommendation> recommendations = recommendationRepository
                .findBestMatchingRecommendations(roleWithSkills.getId(), missingSkill.getId());

            String advice;
            String resource;
            if (!recommendations.isEmpty()) {
                Recommendation rec = recommendations.get(0);
                advice = rec.getAdvice();
                resource = rec.getLearningResource() != null ? rec.getLearningResource() : "Official documentation and practical exercises.";
            } else {
                advice = "Study the fundamentals of " + missingSkill.getName() + " and build hands-on practice projects.";
                resource = "Official documentation and guided tutorials.";
            }

            // Check if user has an existing progress record for this skill
            Optional<UserProgress> progressOpt = userProgressRepository
                .findByUserIdAndSkillId(userId, missingSkill.getId());
            ProgressStatus currentStatus = progressOpt.map(UserProgress::getStatus).orElse(ProgressStatus.NOT_STARTED);

            skillGaps.add(new MissingSkillAdviceDto(
                missingSkill.getId(),
                missingSkill.getName(),
                missingSkill.getCategory(),
                advice,
                resource,
                currentStatus
            ));
        }

        String transparentNote = "This skill comparison is a deterministic rule-based evaluation against the predefined requirements for " 
            + roleWithSkills.getTitle() + ". It serves as a personal learning guide and is not an objective assessment of total employability.";

        return new CareerAdviceResponse(
            user.getId(),
            user.getName(),
            roleWithSkills.getTitle(),
            totalRequired,
            matchedCount,
            missingCount,
            matchPercentage,
            matchedSkillsDto,
            skillGaps,
            transparentNote
        );
    }
}
