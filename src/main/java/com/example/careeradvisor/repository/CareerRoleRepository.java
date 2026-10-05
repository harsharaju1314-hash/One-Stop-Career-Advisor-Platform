package com.example.careeradvisor.repository;

import com.example.careeradvisor.entity.CareerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRoleRepository extends JpaRepository<CareerRole, Long> {

    Optional<CareerRole> findByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCase(String title);

    @Query("SELECT DISTINCT r FROM CareerRole r LEFT JOIN FETCH r.requiredSkills WHERE r.id = :id")
    Optional<CareerRole> findByIdWithSkills(@Param("id") Long id);

    @Query("SELECT DISTINCT r FROM CareerRole r LEFT JOIN FETCH r.requiredSkills ORDER BY r.id ASC")
    List<CareerRole> findAllWithSkills();
}
