package com.reflect.repository;

import com.reflect.domain.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InsightRepository extends JpaRepository<Insight, UUID> {
    Optional<Insight> findByCheckInIdAndUserId(UUID checkInId, UUID userId);
    boolean existsByCheckInId(UUID checkInId);
}
