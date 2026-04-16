package com.reflect.service;

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
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final InsightService insightService;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository,
                          InsightService insightService) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.insightService = insightService;
    }

    @Transactional
    public CheckIn create(UUID userId, CheckInRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
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
