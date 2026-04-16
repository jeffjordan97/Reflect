package com.reflect.repository;

import com.reflect.domain.CheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
    Optional<CheckIn> findByUserIdAndWeekStart(UUID userId, LocalDate weekStart);
    Page<CheckIn> findByUserIdOrderByWeekStartDesc(UUID userId, Pageable pageable);
    Optional<CheckIn> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT c.weekStart FROM CheckIn c WHERE c.user.id = :userId AND c.completed = true ORDER BY c.weekStart DESC")
    List<LocalDate> findCompletedWeekStartsByUserIdDesc(UUID userId);
}
