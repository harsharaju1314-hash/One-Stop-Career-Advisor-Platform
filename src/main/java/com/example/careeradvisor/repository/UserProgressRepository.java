package com.example.careeradvisor.repository;

import com.example.careeradvisor.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    @Query("SELECT p FROM UserProgress p LEFT JOIN FETCH p.skill WHERE p.user.id = :userId ORDER BY p.updatedAt DESC")
    List<UserProgress> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM UserProgress p LEFT JOIN FETCH p.skill WHERE p.user.id = :userId AND p.skill.id = :skillId")
    Optional<UserProgress> findByUserIdAndSkillId(@Param("userId") Long userId, @Param("skillId") Long skillId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    void deleteByUserId(Long userId);
}
