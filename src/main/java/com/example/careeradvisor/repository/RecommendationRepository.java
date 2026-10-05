package com.example.careeradvisor.repository;

import com.example.careeradvisor.entity.CareerRole;
import com.example.careeradvisor.entity.Recommendation;
import com.example.careeradvisor.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findBySkill(Skill skill);

    @Query("SELECT r FROM Recommendation r WHERE r.skill.id = :skillId AND (r.careerRole.id = :roleId OR r.careerRole IS NULL) ORDER BY r.careerRole.id DESC NULLS LAST")
    List<Recommendation> findBestMatchingRecommendations(@Param("roleId") Long roleId, @Param("skillId") Long skillId);

    @Query("SELECT r FROM Recommendation r LEFT JOIN FETCH r.careerRole LEFT JOIN FETCH r.skill")
    List<Recommendation> findAllWithDetails();
}
