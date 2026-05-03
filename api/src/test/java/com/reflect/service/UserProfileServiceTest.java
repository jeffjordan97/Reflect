package com.reflect.service;

import com.reflect.controller.dto.UserProfileRequest;
import com.reflect.domain.User;
import com.reflect.domain.UserProfile;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserProfileRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserRepository userRepository;

    private UserProfileService userProfileService;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userProfileService = new UserProfileService(userProfileRepository, userRepository);
        userId = UUID.randomUUID();
        user = new User("test@example.com", "hash", "Test User");
    }

    @Test
    void createOrUpdate_createsNewProfileWhenNoneExists() {
        var request = new UserProfileRequest(
                "Software Engineer", "Technology", "Senior",
                List.of("Leadership", "Communication"), "Building products"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfile result = userProfileService.createOrUpdate(userId, request);

        assertNotNull(result);
        assertEquals("Software Engineer", result.getProfession());
        assertEquals("Technology", result.getIndustry());
        assertEquals("Senior", result.getRoleLevel());
        assertArrayEquals(new String[]{"Leadership", "Communication"}, result.getFocusAreas());
        assertEquals("Building products", result.getBioContext());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void createOrUpdate_updatesExistingProfile() {
        UserProfile existing = new UserProfile(user);
        existing.setProfession("Old Profession");

        var request = new UserProfileRequest(
                "Product Manager", "Finance", "Lead",
                List.of("Strategy"), "New context"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfile result = userProfileService.createOrUpdate(userId, request);

        assertEquals("Product Manager", result.getProfession());
        assertEquals("Finance", result.getIndustry());
        assertEquals("Lead", result.getRoleLevel());
        verify(userProfileRepository).save(existing);
    }

    @Test
    void getByUserId_returnsEmptyForMissingProfile() {
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<UserProfile> result = userProfileService.getByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void createOrUpdate_throwsBadRequestWhenFocusAreasExceedsMax() {
        List<String> tooManyAreas = IntStream.rangeClosed(1, 11)
                .mapToObj(i -> "Area " + i)
                .toList();
        var request = new UserProfileRequest(
                "Engineer", "Tech", "Mid", tooManyAreas, null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class,
                () -> userProfileService.createOrUpdate(userId, request));
        assertEquals(400, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("10"));
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void createOrUpdate_allowsNullFocusAreas() {
        var request = new UserProfileRequest(
                "Designer", "Media", null, null, null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfile result = userProfileService.createOrUpdate(userId, request);

        assertNull(result.getFocusAreas());
    }

    @Test
    void createOrUpdate_throwsNotFoundForMissingUser() {
        var request = new UserProfileRequest("Engineer", null, null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> userProfileService.createOrUpdate(userId, request));
        assertEquals(404, ex.getStatus().value());
    }
}
