package com.example.careeradvisor.repository;

import com.example.careeradvisor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.targetRole LEFT JOIN FETCH u.skills WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.targetRole LEFT JOIN FETCH u.skills")
    List<User> findAllWithDetails();
}
