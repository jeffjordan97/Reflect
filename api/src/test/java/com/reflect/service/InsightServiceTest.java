package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.Insight;
import com.reflect.domain.User;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.InsightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock private InsightRepository insightRepository;
    @Mock private CheckInRepository checkInRepository;
    @Mock private AnthropicClient anthropicClient;
    @Mock private MonthlyInsightService monthlyInsightService;

    private InsightService insightService;
    private User user;
    private CheckIn checkIn;
    private UUID checkInId;

    @BeforeEach
    void setUp() {
        ReflectProperties.Anthropic anthropic = new ReflectProperties.Anthropic(
                "test-key", "https://api.anthropic.com", "2023-06-01",
                "claude-haiku-4-5-20251001", "claude-sonnet-4-6",
                60, 1200, 1500, 100, 10
        );
        ReflectProperties props = new ReflectProperties(
                null, anthropic, null, null, null, null, null, null, null, null, null
        );
        insightService = new InsightService(
                insightRepository, checkInRepository, anthropicClient, props, monthlyInsightService
        );

        user = new User("test@example.com", "hash", "Test User");
        checkInId = UUID.randomUUID();
        checkIn = new CheckIn(user, LocalDate.now());
        checkIn.setWins("Shipped CI pipeline");
        checkIn.setFriction("Got stuck on Docker networking for a day");
        checkIn.setEnergyRating((short) 6);
        checkIn.setSignalMoment("Productive 1:1 with Sarah about team priorities");
        checkIn.setIntentions("Ship the insights feature");
        checkIn.setCompleted(true);
    }

    @Test
    void generateFor_createsInsightWhenNoneExists() {
        when(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn));
        when(insightRepository.existsByCheckInId(checkInId)).thenReturn(false);
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenReturn(new AnthropicClient.MessageResult(
                        "You mentioned getting stuck on Docker for a day, which pairs with a 6 on energy. Worth noticing what made the Sarah conversation land so differently.",
                        "claude-haiku-4-5-20251001",
                        new AnthropicClient.Usage(200, 45)
                ));
        when(insightRepository.save(any(Insight.class))).thenAnswer(inv -> inv.getArgument(0));

        insightService.generateFor(checkInId);

        ArgumentCaptor<Insight> captor = ArgumentCaptor.forClass(Insight.class);
        verify(insightRepository).save(captor.capture());
        Insight saved = captor.getValue();
        assertNotNull(saved.getContent());
        assertEquals("claude-haiku-4-5-20251001", saved.getModel());
        assertEquals(200, saved.getInputTokens());
        assertEquals(45, saved.getOutputTokens());
    }

    @Test
    void generateFor_skipsWhenInsightAlreadyExists() {
        when(insightRepository.existsByCheckInId(checkInId)).thenReturn(true);

        insightService.generateFor(checkInId);

        verify(insightRepository, never()).save(any());
        verify(anthropicClient, never()).sendMessage(anyString(), anyInt(), anyString(), any());
    }

    @Test
    void generateFor_skipsWhenCheckInNotCompleted() {
        checkIn.setCompleted(false);
        when(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn));
        when(insightRepository.existsByCheckInId(checkInId)).thenReturn(false);

        insightService.generateFor(checkInId);

        verify(anthropicClient, never()).sendMessage(anyString(), anyInt(), anyString(), any());
        verify(insightRepository, never()).save(any());
    }

    @Test
    void generateFor_silentlyHandlesAnthropicFailure() {
        when(checkInRepository.findById(checkInId)).thenReturn(Optional.of(checkIn));
        when(insightRepository.existsByCheckInId(checkInId)).thenReturn(false);
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenThrow(new AnthropicClient.AnthropicException("API rate limited"));

        // Should NOT throw — fails silently
        assertDoesNotThrow(() -> insightService.generateFor(checkInId));

        verify(insightRepository, never()).save(any());
    }

    @Test
    void getByCheckIn_returnsInsightWhenExists() {
        UUID userId = UUID.randomUUID();
        Insight insight = new Insight(user, checkIn, "Great week.", "haiku", 100, 30);
        when(insightRepository.findByCheckInIdAndUserId(checkInId, userId)).thenReturn(Optional.of(insight));

        Optional<Insight> result = insightService.getByCheckIn(checkInId, userId);
        assertTrue(result.isPresent());
        assertEquals("Great week.", result.get().getContent());
    }
}
