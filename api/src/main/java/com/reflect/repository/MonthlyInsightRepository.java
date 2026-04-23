package com.reflect.repository;

import com.reflect.domain.MonthlyInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthlyInsightRepository extends JpaRepository<MonthlyInsight, UUID> {
    List<MonthlyInsight> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<MonthlyInsight> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserId(UUID userId);
}
