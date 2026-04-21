package com.reflect.repository;

import com.reflect.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByStripeCustomerId(String stripeCustomerId);

    @Query("SELECT u FROM User u WHERE u.remindersEnabled = true AND u.emailVerified = true")
    List<User> findEligibleForReminder();
}
