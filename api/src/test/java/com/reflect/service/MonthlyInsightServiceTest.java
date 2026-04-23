package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.MonthlyInsight;
import com.reflect.domain.User;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.MonthlyInsightRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyInsightServiceTest {

    @Mock private MonthlyInsightRepository monthlyInsightRepository;
    @Mock private CheckInRepository checkInRepository;
    @Mock private UserRepository userRepository;
    @Mock private AnthropicClient anthropicClient;

    private MonthlyInsightService service;
    private User user;
    private UUID userId;

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
        service = new MonthlyInsightService(
                monthlyInsightRepository, checkInRepository, userRepository, anthropicClient, props
        );

        user = new User("test@example.com", "hash", "Test User");
        userId = UUID.randomUUID();
    }

    @Test
    void generateIfDue_generatesAfter4thCheckIn() {
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(4L);
        when(monthlyInsightRepository.countByUserId(userId)).thenReturn(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findCompletedByUserIdDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(buildCheckIns(4));
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenReturn(new AnthropicClient.MessageResult(
                        "Over the past month, your energy has steadily climbed.",
                        "claude-haiku-4-5-20251001",
                        new AnthropicClient.Usage(500, 120)
                ));
        when(monthlyInsightRepository.save(any(MonthlyInsight.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.generateIfDue(userId);

        verify(monthlyInsightRepository).save(any(MonthlyInsight.class));
    }

    @Test
    void generateIfDue_skipsWhenNotDue() {
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(3L);
        when(monthlyInsightRepository.countByUserId(userId)).thenReturn(0L);

        service.generateIfDue(userId);

        verify(anthropicClient, never()).sendMessage(anyString(), anyInt(), anyString(), any());
        verify(monthlyInsightRepository, never()).save(any());
    }

    @Test
    void generateIfDue_generatesAfter8thCheckIn() {
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(8L);
        when(monthlyInsightRepository.countByUserId(userId)).thenReturn(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findCompletedByUserIdDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(buildCheckIns(4));
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenReturn(new AnthropicClient.MessageResult(
                        "A clear pattern of increasing intentionality emerges.",
                        "claude-haiku-4-5-20251001",
                        new AnthropicClient.Usage(600, 130)
                ));
        when(monthlyInsightRepository.save(any(MonthlyInsight.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.generateIfDue(userId);

        verify(monthlyInsightRepository).save(any(MonthlyInsight.class));
    }

    @Test
    void generateIfDue_skipsWhenAlreadyGenerated() {
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(4L);
        when(monthlyInsightRepository.countByUserId(userId)).thenReturn(1L);

        service.generateIfDue(userId);

        verify(anthropicClient, never()).sendMessage(anyString(), anyInt(), anyString(), any());
        verify(monthlyInsightRepository, never()).save(any());
    }

    @Test
    void generateMonthlyInsight_buildsCorrectPrompt() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<CheckIn> checkIns = buildCheckIns(4);
        when(checkInRepository.findCompletedByUserIdDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(checkIns);
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenReturn(new AnthropicClient.MessageResult(
                        "Monthly synthesis content.",
                        "claude-haiku-4-5-20251001",
                        new AnthropicClient.Usage(400, 100)
                ));
        when(monthlyInsightRepository.save(any(MonthlyInsight.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.generateMonthlyInsight(userId);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnthropicClient.Message>> messagesCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(anthropicClient).sendMessage(
                eq("claude-haiku-4-5-20251001"),
                eq(1200),
                anyString(),
                messagesCaptor.capture()
        );

        String userMessage = messagesCaptor.getValue().getFirst().content();
        assertTrue(userMessage.contains("Here are the last 4 weekly check-ins"));
        assertTrue(userMessage.contains("**Wins:**"));
        assertTrue(userMessage.contains("**Friction:**"));
        assertTrue(userMessage.contains("**Energy:**"));
        assertTrue(userMessage.contains("**Signal moment:**"));
        assertTrue(userMessage.contains("**Intentions:**"));
        assertTrue(userMessage.contains("Offer a monthly synthesis."));

        // Verify all 4 check-ins are present
        for (CheckIn ci : checkIns) {
            assertTrue(userMessage.contains("Week of " + ci.getWeekStart().format(
                    java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy"))));
        }
    }

    @Test
    void generateMonthlyInsight_silentlyHandlesFailure() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findCompletedByUserIdDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(buildCheckIns(4));
        when(anthropicClient.sendMessage(anyString(), anyInt(), anyString(), any()))
                .thenThrow(new AnthropicClient.AnthropicException("API rate limited"));

        assertDoesNotThrow(() -> service.generateMonthlyInsight(userId));

        verify(monthlyInsightRepository, never()).save(any());
    }

    private List<CheckIn> buildCheckIns(int count) {
        LocalDate baseDate = LocalDate.of(2026, 3, 30);
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    CheckIn ci = new CheckIn(user, baseDate.plusWeeks(count - 1 - i));
                    ci.setWins("Wins for week " + (i + 1));
                    ci.setFriction("Friction for week " + (i + 1));
                    ci.setEnergyRating((short) (5 + i));
                    ci.setSignalMoment("Signal moment for week " + (i + 1));
                    ci.setIntentions("Intentions for week " + (i + 1));
                    ci.setCompleted(true);
                    return ci;
                })
                .toList();
    }
}
