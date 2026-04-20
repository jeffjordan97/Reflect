package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.CheckInRequest;
import com.reflect.domain.CheckIn;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final InsightService insightService;
    private final int maxCheckIns;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository,
                          InsightService insightService, ReflectProperties properties) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.insightService = insightService;
        this.maxCheckIns = properties.freeTier().maxCheckIns();
    }

    @Transactional
    public CheckIn create(UUID userId, CheckInRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (!user.isPro()) {
            long completedCount = checkInRepository.countCompletedByUserId(userId);
            if (completedCount >= maxCheckIns) {
                throw ApiException.forbidden("Free tier limit reached. Upgrade to Pro for unlimited check-ins.");
            }
        }

        LocalDate sunday = currentWeekSunday();
        if (checkInRepository.findByUserIdAndWeekStart(userId, sunday).isPresent()) {
            throw ApiException.conflict("Check-in already exists for this week");
        }
        CheckIn checkIn = new CheckIn(user, sunday);
        applyFields(checkIn, request);
        return checkInRepository.save(checkIn);
    }

    @Transactional
    public CheckIn update(UUID checkInId, UUID userId, CheckInRequest request) {
        CheckIn checkIn = checkInRepository.findByIdAndUserId(checkInId, userId)
                .orElseThrow(() -> ApiException.notFound("Check-in not found"));
        boolean wasCompleted = checkIn.isCompleted();
        applyFields(checkIn, request);
        CheckIn saved = checkInRepository.save(checkIn);
        if (!wasCompleted && saved.isCompleted()) {
            insightService.generateFor(saved.getId());
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<CheckIn> getCurrent(UUID userId) {
        LocalDate sunday = currentWeekSunday();
        return checkInRepository.findByUserIdAndWeekStart(userId, sunday);
    }

    @Transactional(readOnly = true)
    public Optional<CheckIn> getById(UUID id, UUID userId) {
        return checkInRepository.findByIdAndUserId(id, userId);
    }

    @Transactional(readOnly = true)
    public Page<CheckIn> list(UUID userId, Pageable pageable) {
        return checkInRepository.findByUserIdOrderByWeekStartDesc(userId, pageable);
    }

    /**
     * Count consecutive completed weeks going backwards from the current week.
     * A streak breaks on any missing Sunday.
     */
    @Transactional(readOnly = true)
    public int getStreak(UUID userId) {
        List<LocalDate> weeks = checkInRepository.findCompletedWeekStartsByUserIdDesc(userId);
        if (weeks.isEmpty()) return 0;

        LocalDate expected = currentWeekSunday();
        int streak = 0;

        for (LocalDate week : weeks) {
            if (week.equals(expected)) {
                streak++;
                expected = expected.minusWeeks(1);
            } else if (week.isBefore(expected)) {
                // Gap found — streak is broken
                break;
            }
            // week.isAfter(expected) shouldn't happen with DESC ordering, skip
        }

        return streak;
    }

    static LocalDate currentWeekSunday() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    private void applyFields(CheckIn checkIn, CheckInRequest request) {
        if (request.wins() != null) checkIn.setWins(request.wins());
        if (request.friction() != null) checkIn.setFriction(request.friction());
        if (request.energyRating() != null) checkIn.setEnergyRating(request.energyRating());
        if (request.signalMoment() != null) checkIn.setSignalMoment(request.signalMoment());
        if (request.intentions() != null) checkIn.setIntentions(request.intentions());
        if (request.completed() != null) checkIn.setCompleted(request.completed());
    }
}
