package com.reflect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Reflect API — Guided Weekly Review and AI Insight Platform.
 *
 * Entry point for the Spring Boot application.
 *
 * Key annotations:
 *  @SpringBootApplication     — enables component scan, auto-configuration, Spring Boot.
 *  @ConfigurationPropertiesScan — auto-registers all @ConfigurationProperties classes
 *                                 in the com.reflect package (e.g. ReflectProperties).
 *  @EnableAsync               — enables @Async on agent methods and MCP SSE handlers.
 *                               Thread pools configured in AsyncConfig.java.
 *  @EnableScheduling          — enables @Scheduled on job classes
 *                               (ReminderEmailJob, InsightSynthesisAgent, etc.).
 *
 * Architecture:
 *  See docs/adr/ for all architectural decisions.
 *  See CLAUDE.md for coding conventions and layer rules.
 *
 * Package structure (enforced by CLAUDE.md):
 *  com.reflect.api/        — REST controllers (no business logic)
 *  com.reflect.mcp/        — MCP adapter layer (no business logic)
 *  com.reflect.service/    — Business logic (all rules live here)
 *  com.reflect.agent/      — AI agent layer — Phase 3 (delegates to services)
 *  com.reflect.domain/     — JPA entities
 *  com.reflect.repository/ — Spring Data JPA interfaces
 *  com.reflect.scheduler/  — @Scheduled job classes
 *  com.reflect.config/     — Spring configuration beans and @ConfigurationProperties
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
public class ReflectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReflectApplication.class, args);
    }

}
