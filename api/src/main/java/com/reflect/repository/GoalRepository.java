package com.reflect.repository;

import com.reflect.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUserIdOrderBySortOrderAsc(UUID userId);
    List<Goal> findByUserIdAndStatusOrderBySortOrderAsc(UUID userId, String status);
    long countByUserIdAndStatus(UUID userId, String status);
}
