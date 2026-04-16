package com.reflect.service;

import com.reflect.config.ReflectProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Anthropic Messages API.
 * Uses Spring's RestClient directly — no third-party SDK per ADR-005 spirit.
 */
@Component
public class AnthropicClient {

    private static final Logger log = LoggerFactory.getLogger(AnthropicClient.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String apiVersion;

    public AnthropicClient(ReflectProperties properties) {
        ReflectProperties.Anthropic anthropic = properties.anthropic();
        this.apiKey = anthropic.apiKey();
        this.apiVersion = anthropic.apiVersion();
        this.restClient = RestClient.builder()
                .baseUrl(anthropic.baseUrl())
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public record Message(String role, String content) {}

    public record Usage(int inputTokens, int outputTokens) {}

    public record MessageResult(String text, String model, Usage usage) {}

    /**
     * Send a messages request to Claude.
     *
     * @throws AnthropicException if the API call fails or returns an unexpected shape
     */
    public MessageResult sendMessage(String model, int maxTokens, String systemPrompt, List<Message> messages) {
        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", messages.stream()
                        .map(m -> Map.of("role", m.role(), "content", m.content()))
                        .toList()
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", apiVersion)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new AnthropicException("Empty response from Anthropic API");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.get("content");
            if (contentList == null || contentList.isEmpty()) {
                throw new AnthropicException("No content in Anthropic response");
            }

            String text = (String) contentList.get(0).get("text");
            String responseModel = (String) response.get("model");

            @SuppressWarnings("unchecked")
            Map<String, Object> usage = (Map<String, Object>) response.get("usage");
            int inputTokens = usage != null ? ((Number) usage.getOrDefault("input_tokens", 0)).intValue() : 0;
            int outputTokens = usage != null ? ((Number) usage.getOrDefault("output_tokens", 0)).intValue() : 0;

            return new MessageResult(text, responseModel, new Usage(inputTokens, outputTokens));
        } catch (RestClientException e) {
            log.error("Anthropic API call failed: {}", e.getMessage());
            throw new AnthropicException("Anthropic API call failed: " + e.getMessage(), e);
        }
    }

    public static class AnthropicException extends RuntimeException {
        public AnthropicException(String message) { super(message); }
        public AnthropicException(String message, Throwable cause) { super(message, cause); }
    }
}
