package com.reflect.service;

import com.reflect.controller.dto.UserProfileRequest;
import com.reflect.domain.User;
import com.reflect.domain.UserProfile;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserProfileRepository;
import com.reflect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private static final int MAX_FOCUS_AREAS = 10;

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> getByUserId(UUID userId) {
        return userProfileRepository.findByUserId(userId);
    }

    @Transactional
    public UserProfile createOrUpdate(UUID userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (request.focusAreas() != null && request.focusAreas().size() > MAX_FOCUS_AREAS) {
            throw ApiException.badRequest("Focus areas cannot exceed " + MAX_FOCUS_AREAS + " items");
        }

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfile(user));

        profile.setProfession(request.profession());
        profile.setIndustry(request.industry());
        profile.setRoleLevel(request.roleLevel());
        profile.setFocusAreas(request.focusAreas() != null
                ? request.focusAreas().toArray(new String[0])
                : null);
        profile.setBioContext(request.bioContext());

        return userProfileRepository.save(profile);
    }
}
