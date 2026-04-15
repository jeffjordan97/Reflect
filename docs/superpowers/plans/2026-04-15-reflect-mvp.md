# Reflect MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Reflect MVP — user auth (JWT RS256) and weekly check-in CRUD — as a full-stack monorepo with Spring Boot 3.3 backend and Next.js 14 frontend.

**Architecture:** Monorepo with `/api` (Spring Boot 3.3, Java 21) and `/web` (Next.js 14, TypeScript). Backend uses Flyway migrations, JPA entities, and a layered service/controller architecture. Frontend uses App Router, Ark UI for headless components, and Tailwind CSS. JWT access tokens in memory, refresh tokens in httpOnly cookies.

**Tech Stack:** Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA, Flyway, PostgreSQL 15, JJWT (RS256), Next.js 14, TypeScript, Ark UI, Tailwind CSS, Docker Compose.

**Important:** No git commits until all tasks are complete (both backend and frontend foundations in place). The final task handles the initial commit.

---

## File Map

### Backend (`api/`)

| File | Responsibility |
|------|---------------|
| `api/pom.xml` | Maven build config (moved from root) |
| `api/src/main/java/com/reflect/ReflectApplication.java` | Spring Boot entry point (moved from root) |
| `api/src/main/java/com/reflect/config/ReflectProperties.java` | Typed config binding (moved from root) |
| `api/src/main/java/com/reflect/config/SecurityConfig.java` | Spring Security filter chain, CORS, CSRF, session |
| `api/src/main/java/com/reflect/config/JwtProvider.java` | RS256 JWT generation, validation, claim extraction |
| `api/src/main/java/com/reflect/config/JwtAuthenticationFilter.java` | OncePerRequestFilter — extracts JWT, sets SecurityContext |
| `api/src/main/java/com/reflect/domain/User.java` | JPA entity — users table |
| `api/src/main/java/com/reflect/domain/RefreshToken.java` | JPA entity — refresh_tokens table |
| `api/src/main/java/com/reflect/domain/CheckIn.java` | JPA entity — check_ins table |
| `api/src/main/java/com/reflect/repository/UserRepository.java` | Spring Data JPA — user queries |
| `api/src/main/java/com/reflect/repository/RefreshTokenRepository.java` | Spring Data JPA — refresh token queries |
| `api/src/main/java/com/reflect/repository/CheckInRepository.java` | Spring Data JPA — check-in queries |
| `api/src/main/java/com/reflect/service/AuthService.java` | Register, login, refresh, logout business logic |
| `api/src/main/java/com/reflect/service/CheckInService.java` | Check-in CRUD business logic |
| `api/src/main/java/com/reflect/controller/AuthController.java` | REST endpoints for auth |
| `api/src/main/java/com/reflect/controller/CheckInController.java` | REST endpoints for check-ins |
| `api/src/main/java/com/reflect/controller/UserController.java` | REST endpoint for user profile |
| `api/src/main/java/com/reflect/controller/dto/RegisterRequest.java` | Registration request DTO |
| `api/src/main/java/com/reflect/controller/dto/LoginRequest.java` | Login request DTO |
| `api/src/main/java/com/reflect/controller/dto/AuthResponse.java` | Auth response DTO (accessToken + expiresIn) |
| `api/src/main/java/com/reflect/controller/dto/CheckInRequest.java` | Check-in create/update request DTO |
| `api/src/main/java/com/reflect/controller/dto/CheckInResponse.java` | Check-in response DTO |
| `api/src/main/java/com/reflect/controller/dto/UserResponse.java` | User profile response DTO |
| `api/src/main/java/com/reflect/exception/ApiException.java` | Custom exception with HTTP status |
| `api/src/main/java/com/reflect/exception/GlobalExceptionHandler.java` | @ControllerAdvice — consistent error responses |
| `api/src/main/resources/application.yml` | Base config (moved from root) |
| `api/src/main/resources/application-dev.yml` | Dev profile config (moved from root) |
| `api/src/main/resources/application-test.yml` | Test profile config (NEW) |
| `api/src/main/resources/db/migration/V1__create_users.sql` | Users table migration |
| `api/src/main/resources/db/migration/V2__create_refresh_tokens.sql` | Refresh tokens table migration |
| `api/src/main/resources/db/migration/V3__create_check_ins.sql` | Check-ins table migration |
| `api/src/test/java/com/reflect/ReflectApplicationTest.java` | Context loads smoke test |
| `api/src/test/java/com/reflect/config/JwtProviderTest.java` | JWT generation/validation unit tests |
| `api/src/test/java/com/reflect/service/AuthServiceTest.java` | Auth business logic unit tests |
| `api/src/test/java/com/reflect/service/CheckInServiceTest.java` | Check-in business logic unit tests |
| `api/src/test/java/com/reflect/controller/AuthControllerIntegrationTest.java` | Auth endpoint integration tests |
| `api/src/test/java/com/reflect/controller/CheckInControllerIntegrationTest.java` | Check-in endpoint integration tests |
| `api/src/test/resources/application-test.yml` | Test config with Testcontainers |

### Frontend (`web/`)

| File | Responsibility |
|------|---------------|
| `web/package.json` | Dependencies and scripts |
| `web/next.config.js` | Next.js config with API proxy |
| `web/tsconfig.json` | TypeScript config |
| `web/tailwind.config.ts` | Tailwind theme (indigo primary, design tokens) |
| `web/postcss.config.js` | PostCSS with Tailwind |
| `web/src/app/globals.css` | Global styles + Tailwind imports |
| `web/src/app/layout.tsx` | Root layout — AuthProvider, Header, font |
| `web/src/app/page.tsx` | Root redirect (/ → /check-in or /login) |
| `web/src/app/login/page.tsx` | Login form page |
| `web/src/app/register/page.tsx` | Registration form page |
| `web/src/app/check-in/page.tsx` | Check-in landing (wizard or detail) |
| `web/src/app/check-in/new/page.tsx` | Force new/resume wizard |
| `web/src/app/history/page.tsx` | Timeline feed |
| `web/src/app/history/[id]/page.tsx` | Single check-in detail |
| `web/src/lib/types.ts` | Shared TypeScript interfaces |
| `web/src/lib/api.ts` | Fetch wrapper with auth + refresh |
| `web/src/lib/auth.tsx` | AuthProvider context |
| `web/src/components/Header.tsx` | Navigation bar |
| `web/src/components/CheckInWizard.tsx` | 5-step wizard form |
| `web/src/components/CheckInCard.tsx` | Summary card for timeline |
| `web/src/components/CheckInDetail.tsx` | Full read-only check-in view |
| `web/src/components/EnergySlider.tsx` | Ark UI slider for 1-10 rating |

### Root

| File | Responsibility |
|------|---------------|
| `docker-compose.yml` | Stays at root (already exists) |
| `README.md` | Updated with monorepo dev instructions |
| `.gitignore` | Root gitignore for both projects |

---

## Task 1: Monorepo Structure & Backend Scaffolding

**Files:**
- Create: `api/` directory structure
- Move: `pom.xml` → `api/pom.xml`
- Move: `ReflectApplication.java` → `api/src/main/java/com/reflect/ReflectApplication.java`
- Move: `ReflectProperties.java` → `api/src/main/java/com/reflect/config/ReflectProperties.java`
- Move: `application.yml` → `api/src/main/resources/application.yml`
- Move: `application-dev.yml` → `api/src/main/resources/application-dev.yml`
- Create: `api/src/main/resources/application-test.yml`
- Create: `api/src/test/resources/application-test.yml`
- Create: `.gitignore`

- [ ] **Step 1: Create the api directory structure**

```bash
mkdir -p api/src/main/java/com/reflect/config
mkdir -p api/src/main/java/com/reflect/domain
mkdir -p api/src/main/java/com/reflect/repository
mkdir -p api/src/main/java/com/reflect/service
mkdir -p api/src/main/java/com/reflect/controller/dto
mkdir -p api/src/main/java/com/reflect/exception
mkdir -p api/src/main/resources/db/migration
mkdir -p api/src/test/java/com/reflect/config
mkdir -p api/src/test/java/com/reflect/service
mkdir -p api/src/test/java/com/reflect/controller
mkdir -p api/src/test/resources
```

- [ ] **Step 2: Move existing backend files into api/**

```bash
mv pom.xml api/pom.xml
mv ReflectApplication.java api/src/main/java/com/reflect/ReflectApplication.java
mv ReflectProperties.java api/src/main/java/com/reflect/config/ReflectProperties.java
mv application.yml api/src/main/resources/application.yml
mv application-dev.yml api/src/main/resources/application-dev.yml
```

Do NOT move `application-prod.yml` into the api resources yet — it references Phase 2+ secrets. Keep it at root as reference. Do NOT move `docker-compose.yml` — it stays at root. Do NOT move the `.docx` and `.md` PRD/SDD docs — they stay at root or in `docs/`.

- [ ] **Step 3: Create application-test.yml for Testcontainers**

Create `api/src/main/resources/application-test.yml` AND `api/src/test/resources/application-test.yml` (same content — test profile needs to be on both classpaths):

```yaml
# application-test.yml
# Active during tests — uses Testcontainers for Postgres
spring:
  datasource:
    # Testcontainers JDBC URL — auto-starts a PostgreSQL 15 container
    url: jdbc:tc:postgresql:15-alpine:///reflect_test
    username: reflect
    password: reflect
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
  # Disable Redis in tests (not needed for MVP)
  data:
    redis:
      repositories:
        enabled: false
  cache:
    type: none
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration

# Disable Sentry in tests
sentry:
  dsn:
  enabled: false

# Test-specific Reflect properties
reflect:
  jwt:
    # Test RSA keys — generated at test startup by JwtProvider tests
    private-key: ${JWT_PRIVATE_KEY:}
    public-key: ${JWT_PUBLIC_KEY:}
    access-token-expiry-seconds: 3600
    refresh-token-expiry-seconds: 86400
  security:
    bcrypt-strength: 4
    max-login-attempts: 5
    lockout-minutes: 15
    reset-token-ttl-hours: 24
    verify-token-ttl-hours: 48
    api-key-rotation-days: 90
  # Provide defaults for unused Phase 2+ config to prevent startup failures
  anthropic:
    api-key: test-placeholder
    base-url: https://api.anthropic.com
    api-version: "2023-06-01"
    model-haiku: test-model
    model-sonnet: test-model
    timeout-seconds: 10
    max-tokens-monthly-insight: 100
    max-tokens-quarterly-insight: 100
    max-tokens-nudge: 100
    max-tokens-interpersonal-classify: 10
  stripe:
    secret-key: sk_test_placeholder
    webhook-secret: whsec_placeholder
    price-id-monthly: price_test
    price-id-annual: price_test
  sendgrid:
    api-key: test-placeholder
    from-email: test@reflect.app
    from-name: Reflect Test
    template-welcome: d-test
    template-sunday-reminder: d-test
    template-monthly-digest: d-test
    template-nudge: d-test
    template-billing-alert: d-test
    template-password-reset: d-test
    template-email-verify: d-test
  free-tier:
    max-check-ins: 4
  reminder:
    default-time: "09:00"
    default-day: SUNDAY
  agent:
    min-entries-for-pattern: 4
    nudge-strength-threshold: 7
    nudge-cooldown-days: 7
    avoidance-word-count-threshold: 30
  mcp:
    rate-limit-per-hour: 60

logging:
  level:
    root: WARN
    com.reflect: DEBUG
```

- [ ] **Step 4: Create root .gitignore**

Create `.gitignore` at the project root:

```gitignore
# Java / Maven
api/target/
*.class
*.jar
*.war
*.ear
*.log
hs_err_pid*

# IDE
.idea/
*.iml
.vscode/
*.swp
*.swo

# macOS
.DS_Store

# Environment
.env
.env.local
*.pem

# Next.js
web/.next/
web/out/
web/node_modules/

# Node
node_modules/

# Superpowers brainstorm sessions
.superpowers/

# Docker volumes (local only)
data/

# Test output
api/target/surefire-reports/
api/target/failsafe-reports/
```

- [ ] **Step 5: Verify the backend compiles**

```bash
cd api && mvn compile -q
```

Expected: BUILD SUCCESS. If it fails on missing env vars for `ReflectProperties`, that's expected — the compile step doesn't bind config. If it fails for another reason, fix before proceeding.

---

## Task 2: Database Migrations

**Files:**
- Create: `api/src/main/resources/db/migration/V1__create_users.sql`
- Create: `api/src/main/resources/db/migration/V2__create_refresh_tokens.sql`
- Create: `api/src/main/resources/db/migration/V3__create_check_ins.sql`

- [ ] **Step 1: Create V1 — users table**

Create `api/src/main/resources/db/migration/V1__create_users.sql`:

```sql
CREATE TABLE users (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    display_name   VARCHAR(100) NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);
```

- [ ] **Step 2: Create V2 — refresh_tokens table**

Create `api/src/main/resources/db/migration/V2__create_refresh_tokens.sql`:

```sql
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
```

- [ ] **Step 3: Create V3 — check_ins table**

Create `api/src/main/resources/db/migration/V3__create_check_ins.sql`:

```sql
CREATE TABLE check_ins (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start     DATE        NOT NULL,
    wins           TEXT,
    friction       TEXT,
    energy_rating  SMALLINT    CHECK (energy_rating BETWEEN 1 AND 10),
    signal_moment  TEXT,
    intentions     TEXT,
    completed      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_check_ins_user_week UNIQUE (user_id, week_start)
);

CREATE INDEX idx_check_ins_user_id ON check_ins (user_id);
CREATE INDEX idx_check_ins_user_week ON check_ins (user_id, week_start DESC);
```

---

## Task 3: JPA Domain Entities

**Files:**
- Create: `api/src/main/java/com/reflect/domain/User.java`
- Create: `api/src/main/java/com/reflect/domain/RefreshToken.java`
- Create: `api/src/main/java/com/reflect/domain/CheckIn.java`

- [ ] **Step 1: Create User entity**

Create `api/src/main/java/com/reflect/domain/User.java`:

```java
package com.reflect.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected User() {}

    public User(String email, String passwordHash, String displayName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
```

- [ ] **Step 2: Create RefreshToken entity**

Create `api/src/main/java/com/reflect/domain/RefreshToken.java`:

```java
package com.reflect.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected RefreshToken() {}

    public RefreshToken(User user, String tokenHash, OffsetDateTime expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getTokenHash() { return tokenHash; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }
}
```

- [ ] **Step 3: Create CheckIn entity**

Create `api/src/main/java/com/reflect/domain/CheckIn.java`:

```java
package com.reflect.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(columnDefinition = "TEXT")
    private String wins;

    @Column(columnDefinition = "TEXT")
    private String friction;

    @Column(name = "energy_rating")
    private Short energyRating;

    @Column(name = "signal_moment", columnDefinition = "TEXT")
    private String signalMoment;

    @Column(columnDefinition = "TEXT")
    private String intentions;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected CheckIn() {}

    public CheckIn(User user, LocalDate weekStart) {
        this.user = user;
        this.weekStart = weekStart;
        this.completed = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public LocalDate getWeekStart() { return weekStart; }
    public String getWins() { return wins; }
    public String getFriction() { return friction; }
    public Short getEnergyRating() { return energyRating; }
    public String getSignalMoment() { return signalMoment; }
    public String getIntentions() { return intentions; }
    public boolean isCompleted() { return completed; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setWins(String wins) { this.wins = wins; }
    public void setFriction(String friction) { this.friction = friction; }
    public void setEnergyRating(Short energyRating) { this.energyRating = energyRating; }
    public void setSignalMoment(String signalMoment) { this.signalMoment = signalMoment; }
    public void setIntentions(String intentions) { this.intentions = intentions; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
```

---

## Task 4: Spring Data Repositories

**Files:**
- Create: `api/src/main/java/com/reflect/repository/UserRepository.java`
- Create: `api/src/main/java/com/reflect/repository/RefreshTokenRepository.java`
- Create: `api/src/main/java/com/reflect/repository/CheckInRepository.java`

- [ ] **Step 1: Create UserRepository**

Create `api/src/main/java/com/reflect/repository/UserRepository.java`:

```java
package com.reflect.repository;

import com.reflect.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

- [ ] **Step 2: Create RefreshTokenRepository**

Create `api/src/main/java/com/reflect/repository/RefreshTokenRepository.java`:

```java
package com.reflect.repository;

import com.reflect.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    void deleteAllByUserId(UUID userId);
}
```

- [ ] **Step 3: Create CheckInRepository**

Create `api/src/main/java/com/reflect/repository/CheckInRepository.java`:

```java
package com.reflect.repository;

import com.reflect.domain.CheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
    Optional<CheckIn> findByUserIdAndWeekStart(UUID userId, LocalDate weekStart);
    Page<CheckIn> findByUserIdOrderByWeekStartDesc(UUID userId, Pageable pageable);
    Optional<CheckIn> findByIdAndUserId(UUID id, UUID userId);
}
```

---

## Task 5: Request/Response DTOs

**Files:**
- Create: `api/src/main/java/com/reflect/controller/dto/RegisterRequest.java`
- Create: `api/src/main/java/com/reflect/controller/dto/LoginRequest.java`
- Create: `api/src/main/java/com/reflect/controller/dto/AuthResponse.java`
- Create: `api/src/main/java/com/reflect/controller/dto/CheckInRequest.java`
- Create: `api/src/main/java/com/reflect/controller/dto/CheckInResponse.java`
- Create: `api/src/main/java/com/reflect/controller/dto/UserResponse.java`

- [ ] **Step 1: Create RegisterRequest**

Create `api/src/main/java/com/reflect/controller/dto/RegisterRequest.java`:

```java
package com.reflect.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(max = 100) String displayName
) {}
```

- [ ] **Step 2: Create LoginRequest**

Create `api/src/main/java/com/reflect/controller/dto/LoginRequest.java`:

```java
package com.reflect.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
```

- [ ] **Step 3: Create AuthResponse**

Create `api/src/main/java/com/reflect/controller/dto/AuthResponse.java`:

```java
package com.reflect.controller.dto;

public record AuthResponse(
        String accessToken,
        long expiresIn
) {}
```

- [ ] **Step 4: Create CheckInRequest**

Create `api/src/main/java/com/reflect/controller/dto/CheckInRequest.java`:

```java
package com.reflect.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CheckInRequest(
        @Size(max = 5000) String wins,
        @Size(max = 5000) String friction,
        @Min(1) @Max(10) Short energyRating,
        @Size(max = 5000) String signalMoment,
        @Size(max = 5000) String intentions,
        Boolean completed
) {}
```

- [ ] **Step 5: Create CheckInResponse**

Create `api/src/main/java/com/reflect/controller/dto/CheckInResponse.java`:

```java
package com.reflect.controller.dto;

import com.reflect.domain.CheckIn;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckInResponse(
        UUID id,
        LocalDate weekStart,
        String wins,
        String friction,
        Short energyRating,
        String signalMoment,
        String intentions,
        boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CheckInResponse from(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getWeekStart(),
                checkIn.getWins(),
                checkIn.getFriction(),
                checkIn.getEnergyRating(),
                checkIn.getSignalMoment(),
                checkIn.getIntentions(),
                checkIn.isCompleted(),
                checkIn.getCreatedAt(),
                checkIn.getUpdatedAt()
        );
    }
}
```

- [ ] **Step 6: Create UserResponse**

Create `api/src/main/java/com/reflect/controller/dto/UserResponse.java`:

```java
package com.reflect.controller.dto;

import com.reflect.domain.User;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }
}
```

---

## Task 6: Error Handling

**Files:**
- Create: `api/src/main/java/com/reflect/exception/ApiException.java`
- Create: `api/src/main/java/com/reflect/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Create ApiException**

Create `api/src/main/java/com/reflect/exception/ApiException.java`:

```java
package com.reflect.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }
}
```

- [ ] **Step 2: Create GlobalExceptionHandler**

Create `api/src/main/java/com/reflect/exception/GlobalExceptionHandler.java`:

```java
package com.reflect.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "error", ex.getMessage(),
                "status", ex.getStatus().value(),
                "timestamp", OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation failed",
                "details", errors,
                "status", 400,
                "timestamp", OffsetDateTime.now()
        ));
    }
}
```

---

## Task 7: JWT Infrastructure

**Files:**
- Create: `api/src/main/java/com/reflect/config/JwtProvider.java`
- Test: `api/src/test/java/com/reflect/config/JwtProviderTest.java`

- [ ] **Step 1: Write JwtProvider tests**

Create `api/src/test/java/com/reflect/config/JwtProviderTest.java`:

```java
package com.reflect.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private static JwtProvider jwtProvider;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";

        ReflectProperties.Jwt jwtProps = new ReflectProperties.Jwt(
                privateKeyPem, publicKeyPem, 3600, 86400
        );
        jwtProvider = new JwtProvider(jwtProps);
    }

    @Test
    void generateToken_containsSubjectClaim() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "test@example.com");

        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUserIdFromToken(token));
    }

    @Test
    void validateToken_returnsFalseForTamperedToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "test@example.com");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(jwtProvider.validateToken(tampered));
    }

    @Test
    void validateToken_returnsFalseForGarbage() {
        assertFalse(jwtProvider.validateToken("not.a.jwt"));
    }

    @Test
    void getUserIdFromToken_returnsCorrectId() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "user@example.com");

        assertEquals(userId, jwtProvider.getUserIdFromToken(token));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
cd api && mvn test -pl . -Dtest=JwtProviderTest -q
```

Expected: FAIL — `JwtProvider` class does not exist yet.

- [ ] **Step 3: Implement JwtProvider**

Create `api/src/main/java/com/reflect/config/JwtProvider.java`:

```java
package com.reflect.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpiryMs;

    public JwtProvider(ReflectProperties.Jwt jwtProperties) {
        this.privateKey = parsePrivateKey(jwtProperties.privateKey());
        this.publicKey = parsePublicKey(jwtProperties.publicKey());
        this.accessTokenExpiryMs = jwtProperties.accessTokenExpirySeconds() * 1000L;
    }

    public String generateAccessToken(UUID userId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiryMs))
                .signWith(privateKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public long getAccessTokenExpirySeconds() {
        return accessTokenExpiryMs / 1000;
    }

    private static PrivateKey parsePrivateKey(String pem) {
        try {
            String base64 = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse RSA private key", e);
        }
    }

    private static PublicKey parsePublicKey(String pem) {
        try {
            String base64 = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse RSA public key", e);
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd api && mvn test -pl . -Dtest=JwtProviderTest -q
```

Expected: All 4 tests PASS.

---

## Task 8: Security Configuration

**Files:**
- Create: `api/src/main/java/com/reflect/config/JwtAuthenticationFilter.java`
- Create: `api/src/main/java/com/reflect/config/SecurityConfig.java`

- [ ] **Step 1: Create JwtAuthenticationFilter**

Create `api/src/main/java/com/reflect/config/JwtAuthenticationFilter.java`:

```java
package com.reflect.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtProvider.validateToken(token)) {
                UUID userId = jwtProvider.getUserIdFromToken(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 2: Create SecurityConfig**

Create `api/src/main/java/com/reflect/config/SecurityConfig.java`:

```java
package com.reflect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final int bcryptStrength;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            ReflectProperties properties
    ) {
        this.jwtFilter = jwtFilter;
        this.bcryptStrength = properties.security().bcryptStrength();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

---

## Task 9: Auth Service (TDD)

**Files:**
- Create: `api/src/main/java/com/reflect/service/AuthService.java`
- Test: `api/src/test/java/com/reflect/service/AuthServiceTest.java`

- [ ] **Step 1: Write AuthService unit tests**

Create `api/src/test/java/com/reflect/service/AuthServiceTest.java`:

```java
package com.reflect.service;

import com.reflect.config.JwtProvider;
import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.AuthResponse;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import com.reflect.domain.RefreshToken;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.RefreshTokenRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtProvider jwtProvider;

    private AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @BeforeEach
    void setUp() {
        ReflectProperties.Jwt jwtProps = new ReflectProperties.Jwt("", "", 3600, 604800);
        authService = new AuthService(
                userRepository, refreshTokenRepository,
                jwtProvider, passwordEncoder, jwtProps
        );
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        var request = new RegisterRequest("test@example.com", "password123", "Test User");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateAccessToken(any(), eq("test@example.com"))).thenReturn("access-token");
        when(jwtProvider.getAccessTokenExpirySeconds()).thenReturn(3600L);

        AuthService.AuthResult result = authService.register(request);

        assertNotNull(result);
        assertEquals("access-token", result.authResponse().accessToken());
        assertEquals(3600, result.authResponse().expiresIn());
        assertNotNull(result.rawRefreshToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("test@example.com", userCaptor.getValue().getEmail());
        assertTrue(passwordEncoder.matches("password123", userCaptor.getValue().getPasswordHash()));
    }

    @Test
    void register_throwsConflictForDuplicateEmail() {
        var request = new RegisterRequest("exists@example.com", "password123", "User");
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));
        assertEquals(409, ex.getStatus().value());
    }

    @Test
    void login_returnsTokensForValidCredentials() {
        var request = new LoginRequest("test@example.com", "password123");
        User user = new User("test@example.com", passwordEncoder.encode("password123"), "Test");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(any(), eq("test@example.com"))).thenReturn("access-token");
        when(jwtProvider.getAccessTokenExpirySeconds()).thenReturn(3600L);

        AuthService.AuthResult result = authService.login(request);

        assertNotNull(result);
        assertEquals("access-token", result.authResponse().accessToken());
    }

    @Test
    void login_throwsUnauthorizedForWrongPassword() {
        var request = new LoginRequest("test@example.com", "wrong-password");
        User user = new User("test@example.com", passwordEncoder.encode("correct-password"), "Test");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(401, ex.getStatus().value());
    }

    @Test
    void login_throwsUnauthorizedForNonexistentEmail() {
        var request = new LoginRequest("ghost@example.com", "password");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(401, ex.getStatus().value());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
cd api && mvn test -pl . -Dtest=AuthServiceTest -q
```

Expected: FAIL — `AuthService` class does not exist yet.

- [ ] **Step 3: Implement AuthService**

Create `api/src/main/java/com/reflect/service/AuthService.java`:

```java
package com.reflect.service;

import com.reflect.config.JwtProvider;
import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.AuthResponse;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import com.reflect.domain.RefreshToken;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.RefreshTokenRepository;
import com.reflect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenExpirySeconds;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            ReflectProperties.Jwt jwtProperties
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpirySeconds = jwtProperties.refreshTokenExpirySeconds();
    }

    public record AuthResult(AuthResponse authResponse, String rawRefreshToken) {}

    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Email already registered");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), hash, request.displayName());
        user = userRepository.save(user);

        return createTokens(user);
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password");
        }

        return createTokens(user);
    }

    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        if (existing.isExpired()) {
            refreshTokenRepository.delete(existing);
            throw ApiException.unauthorized("Refresh token expired");
        }

        // Rotate: delete old, create new
        refreshTokenRepository.delete(existing);
        User user = existing.getUser();

        return createTokens(user);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResult createTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        long expiresIn = jwtProvider.getAccessTokenExpirySeconds();

        String rawRefreshToken = UUID.randomUUID().toString();
        String refreshHash = hashToken(rawRefreshToken);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(refreshTokenExpirySeconds);

        RefreshToken refreshToken = new RefreshToken(user, refreshHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new AuthResult(new AuthResponse(accessToken, expiresIn), rawRefreshToken);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd api && mvn test -pl . -Dtest=AuthServiceTest -q
```

Expected: All 5 tests PASS.

---

## Task 10: Auth Controller

**Files:**
- Create: `api/src/main/java/com/reflect/controller/AuthController.java`
- Test: `api/src/test/java/com/reflect/controller/AuthControllerIntegrationTest.java`

- [ ] **Step 1: Create AuthController**

Create `api/src/main/java/com/reflect/controller/AuthController.java`:

```java
package com.reflect.controller;

import com.reflect.controller.dto.AuthResponse;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import com.reflect.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "reflect_refresh_token";
    private static final int REFRESH_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthService.AuthResult result = authService.register(request);
        addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.authResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthService.AuthResult result = authService.login(request);
        addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.ok(result.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractRefreshCookie(request);
        AuthService.AuthResult result = authService.refresh(refreshToken);
        addRefreshCookie(response, result.rawRefreshToken());
        return ResponseEntity.ok(result.authResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractRefreshCookie(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production behind HTTPS
        cookie.setPath("/api/auth");
        cookie.setMaxAge(REFRESH_COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
```

- [ ] **Step 2: Write AuthController integration test**

Create `api/src/test/java/com/reflect/controller/AuthControllerIntegrationTest.java`:

```java
package com.reflect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflect.controller.dto.LoginRequest;
import com.reflect.controller.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void register_returns201WithTokens() throws Exception {
        var request = new RegisterRequest("newuser@example.com", "password123", "New User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(cookie().exists("reflect_refresh_token"))
                .andExpect(cookie().httpOnly("reflect_refresh_token", true));
    }

    @Test
    void register_returns409ForDuplicateEmail() throws Exception {
        var request = new RegisterRequest("duplicate@example.com", "password123", "User");

        // First registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Second registration — should fail
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_returns200ForValidCredentials() throws Exception {
        // Register first
        var registerReq = new RegisterRequest("login@example.com", "password123", "User");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        // Login
        var loginReq = new LoginRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void login_returns401ForWrongPassword() throws Exception {
        var registerReq = new RegisterRequest("wrongpw@example.com", "correct", "User");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        var loginReq = new LoginRequest("wrongpw@example.com", "incorrect");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_rotatesTokens() throws Exception {
        var registerReq = new RegisterRequest("refresh@example.com", "password123", "User");
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();

        Cookie refreshCookie = registerResult.getResponse().getCookie("reflect_refresh_token");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("reflect_refresh_token"));
    }

    @Test
    void register_returns400ForInvalidEmail() throws Exception {
        var request = new RegisterRequest("not-an-email", "password123", "User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
```

- [ ] **Step 3: Run the integration tests**

```bash
cd api && mvn verify -pl . -Dtest=AuthControllerIntegrationTest -DfailIfNoTests=false -Dit.test=AuthControllerIntegrationTest -q
```

Expected: All 6 tests PASS. Note: this requires Docker running for Testcontainers. If Docker is not available, skip and verify after Docker setup in Task 20.

---

## Task 11: Check-In Service (TDD)

**Files:**
- Create: `api/src/main/java/com/reflect/service/CheckInService.java`
- Test: `api/src/test/java/com/reflect/service/CheckInServiceTest.java`

- [ ] **Step 1: Write CheckInService unit tests**

Create `api/src/test/java/com/reflect/service/CheckInServiceTest.java`:

```java
package com.reflect.service;

import com.reflect.controller.dto.CheckInRequest;
import com.reflect.domain.CheckIn;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock private CheckInRepository checkInRepository;
    @Mock private UserRepository userRepository;

    private CheckInService checkInService;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        checkInService = new CheckInService(checkInRepository, userRepository);
        userId = UUID.randomUUID();
        user = new User("test@example.com", "hash", "Test User");
    }

    @Test
    void create_createsNewCheckInForCurrentWeek() {
        var request = new CheckInRequest("Won a deal", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.create(userId, request);

        assertNotNull(result);
        assertEquals(sunday, result.getWeekStart());
        assertEquals("Won a deal", result.getWins());
        assertFalse(result.isCompleted());
    }

    @Test
    void create_throwsConflictIfCheckInAlreadyExists() {
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn existing = new CheckIn(user, sunday);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.of(existing));

        ApiException ex = assertThrows(ApiException.class, () -> checkInService.create(userId, request));
        assertEquals(409, ex.getStatus().value());
    }

    @Test
    void update_updatesExistingCheckIn() {
        UUID checkInId = UUID.randomUUID();
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        checkIn.setWins("Old wins");

        var request = new CheckInRequest("New wins", "Some friction", (short) 7, null, null, null);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.update(checkInId, userId, request);

        assertEquals("New wins", result.getWins());
        assertEquals("Some friction", result.getFriction());
        assertEquals((short) 7, result.getEnergyRating());
    }

    @Test
    void update_throwsNotFoundForWrongUser() {
        UUID checkInId = UUID.randomUUID();
        var request = new CheckInRequest(null, null, null, null, null, null);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> checkInService.update(checkInId, userId, request));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void getCurrent_returnsCheckInForCurrentWeek() {
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);

        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.of(checkIn));

        Optional<CheckIn> result = checkInService.getCurrent(userId);
        assertTrue(result.isPresent());
    }

    @Test
    void list_returnsPaginatedResults() {
        CheckIn checkIn = new CheckIn(user, LocalDate.now());
        Page<CheckIn> page = new PageImpl<>(List.of(checkIn));

        when(checkInRepository.findByUserIdOrderByWeekStartDesc(eq(userId), any()))
                .thenReturn(page);

        Page<CheckIn> result = checkInService.list(userId, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
cd api && mvn test -pl . -Dtest=CheckInServiceTest -q
```

Expected: FAIL — `CheckInService` class does not exist yet.

- [ ] **Step 3: Implement CheckInService**

Create `api/src/main/java/com/reflect/service/CheckInService.java`:

```java
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

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
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

        applyFields(checkIn, request);
        return checkInRepository.save(checkIn);
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
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd api && mvn test -pl . -Dtest=CheckInServiceTest -q
```

Expected: All 6 tests PASS.

---

## Task 12: Check-In Controller

**Files:**
- Create: `api/src/main/java/com/reflect/controller/CheckInController.java`
- Test: `api/src/test/java/com/reflect/controller/CheckInControllerIntegrationTest.java`

- [ ] **Step 1: Create CheckInController**

Create `api/src/main/java/com/reflect/controller/CheckInController.java`:

```java
package com.reflect.controller;

import com.reflect.controller.dto.CheckInRequest;
import com.reflect.controller.dto.CheckInResponse;
import com.reflect.domain.CheckIn;
import com.reflect.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @GetMapping
    public ResponseEntity<Page<CheckInResponse>> list(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CheckInResponse> results = checkInService
                .list(userId, PageRequest.of(page, size))
                .map(CheckInResponse::from);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/current")
    public ResponseEntity<CheckInResponse> getCurrent(@AuthenticationPrincipal UUID userId) {
        return checkInService.getCurrent(userId)
                .map(CheckInResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CheckInResponse> create(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckIn checkIn = checkInService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CheckInResponse.from(checkIn));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckInResponse> getById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        return checkInService.getById(id, userId)
                .map(CheckInResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckInResponse> update(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckIn checkIn = checkInService.update(id, userId, request);
        return ResponseEntity.ok(CheckInResponse.from(checkIn));
    }
}
```

- [ ] **Step 2: Write CheckInController integration test**

Create `api/src/test/java/com/reflect/controller/CheckInControllerIntegrationTest.java`:

```java
package com.reflect.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflect.controller.dto.CheckInRequest;
import com.reflect.controller.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckInControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register a fresh user and get an access token
        String unique = "checkin-" + System.nanoTime() + "@example.com";
        var registerReq = new RegisterRequest(unique, "password123", "Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        accessToken = body.get("accessToken").asText();
    }

    @Test
    void getCurrent_returns404WhenNoCheckIn() throws Exception {
        mockMvc.perform(get("/api/check-ins/current")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns201() throws Exception {
        var request = new CheckInRequest("Shipped feature X", null, null, null, null, null);

        mockMvc.perform(post("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wins").value("Shipped feature X"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void update_savesProgress() throws Exception {
        // Create
        var createReq = new CheckInRequest("Wins", null, null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String checkInId = created.get("id").asText();

        // Update with more fields
        var updateReq = new CheckInRequest(null, "Felt stuck on CI", (short) 6, null, null, null);
        mockMvc.perform(put("/api/check-ins/" + checkInId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wins").value("Wins"))
                .andExpect(jsonPath("$.friction").value("Felt stuck on CI"))
                .andExpect(jsonPath("$.energyRating").value(6));
    }

    @Test
    void list_returnsPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/check-ins")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void endpoints_return401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/check-ins/current"))
                .andExpect(status().isUnauthorized());
    }
}
```

- [ ] **Step 3: Run the integration tests**

```bash
cd api && mvn verify -pl . -Dit.test=CheckInControllerIntegrationTest -DfailIfNoTests=false -q
```

Expected: All 5 tests PASS (requires Docker for Testcontainers).

---

## Task 13: User Controller

**Files:**
- Create: `api/src/main/java/com/reflect/controller/UserController.java`

- [ ] **Step 1: Create UserController**

Create `api/src/main/java/com/reflect/controller/UserController.java`:

```java
package com.reflect.controller;

import com.reflect.controller.dto.UserResponse;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UUID userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiException.notFound("User not found"));
    }
}
```

---

## Task 14: Backend Smoke Test

**Files:**
- Create: `api/src/test/java/com/reflect/ReflectApplicationTest.java`

- [ ] **Step 1: Create context-loads smoke test**

Create `api/src/test/java/com/reflect/ReflectApplicationTest.java`:

```java
package com.reflect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ReflectApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the entire Spring context starts without errors
    }
}
```

- [ ] **Step 2: Run full backend test suite**

```bash
cd api && mvn verify -q
```

Expected: BUILD SUCCESS. All unit tests and integration tests pass. If tests fail due to missing RSA keys in the test profile, generate test keys inline (see Task 7 JwtProviderTest for the pattern) or add test keys to `application-test.yml`.

---

## Task 15: Next.js Project Setup

**Files:**
- Create: `web/` directory with Next.js 14, TypeScript, Tailwind CSS, Ark UI

- [ ] **Step 1: Scaffold Next.js project**

```bash
cd /path/to/Reflect
npx create-next-app@14 web --typescript --tailwind --eslint --app --src-dir --no-import-alias
```

When prompted, accept defaults. This creates the `web/` directory with App Router, TypeScript, and Tailwind CSS pre-configured.

- [ ] **Step 2: Install Ark UI**

```bash
cd web && npm install @ark-ui/react
```

- [ ] **Step 3: Configure Tailwind theme with design tokens**

Replace `web/tailwind.config.ts` with:

```typescript
import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#EEF2FF",
          100: "#E0E7FF",
          200: "#C7D2FE",
          300: "#A5B4FC",
          400: "#818CF8",
          500: "#6366F1",
          600: "#4F46E5",
          700: "#4338CA",
          800: "#3730A3",
          900: "#312E81",
        },
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};

export default config;
```

- [ ] **Step 4: Configure Next.js API proxy**

Replace `web/next.config.js` (or `next.config.mjs` if scaffolded as ESM) with:

```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: "http://localhost:8080/api/:path*",
      },
    ];
  },
};

module.exports = nextConfig;
```

- [ ] **Step 5: Set up global styles**

Replace `web/src/app/globals.css` with:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  body {
    @apply bg-white text-gray-900 antialiased;
  }
}
```

- [ ] **Step 6: Verify the frontend builds**

```bash
cd web && npm run build
```

Expected: Build succeeds with no errors.

---

## Task 16: TypeScript Types & API Client

**Files:**
- Create: `web/src/lib/types.ts`
- Create: `web/src/lib/api.ts`

- [ ] **Step 1: Create shared TypeScript types**

Create `web/src/lib/types.ts`:

```typescript
export interface AuthResponse {
  accessToken: string;
  expiresIn: number;
}

export interface UserResponse {
  id: string;
  email: string;
  displayName: string;
  createdAt: string;
}

export interface CheckInResponse {
  id: string;
  weekStart: string;
  wins: string | null;
  friction: string | null;
  energyRating: number | null;
  signalMoment: string | null;
  intentions: string | null;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CheckInRequest {
  wins?: string;
  friction?: string;
  energyRating?: number;
  signalMoment?: string;
  intentions?: string;
  completed?: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
}

export interface ApiError {
  error: string;
  status: number;
}
```

- [ ] **Step 2: Create API client with auth refresh**

Create `web/src/lib/api.ts`:

```typescript
import { AuthResponse, ApiError } from "./types";

let accessToken: string | null = null;
let refreshPromise: Promise<string | null> | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function getAccessToken(): string | null {
  return accessToken;
}

async function refreshAccessToken(): Promise<string | null> {
  try {
    const res = await fetch("/api/auth/refresh", {
      method: "POST",
      credentials: "include",
    });
    if (!res.ok) return null;
    const data: AuthResponse = await res.json();
    accessToken = data.accessToken;
    return accessToken;
  } catch {
    return null;
  }
}

async function ensureToken(): Promise<string | null> {
  if (!accessToken) {
    if (!refreshPromise) {
      refreshPromise = refreshAccessToken().finally(() => {
        refreshPromise = null;
      });
    }
    return refreshPromise;
  }
  return accessToken;
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = await ensureToken();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  let res = await fetch(path, {
    ...options,
    headers,
    credentials: "include",
  });

  // If 401, try refreshing once
  if (res.status === 401 && token) {
    const newToken = await refreshAccessToken();
    if (newToken) {
      headers["Authorization"] = `Bearer ${newToken}`;
      res = await fetch(path, {
        ...options,
        headers,
        credentials: "include",
      });
    }
  }

  if (!res.ok) {
    const error: ApiError = await res.json().catch(() => ({
      error: "Request failed",
      status: res.status,
    }));
    throw error;
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}
```

---

## Task 17: Auth Provider

**Files:**
- Create: `web/src/lib/auth.tsx`

- [ ] **Step 1: Create AuthProvider context**

Create `web/src/lib/auth.tsx`:

```tsx
"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { apiFetch, setAccessToken, getAccessToken } from "./api";
import type { AuthResponse, UserResponse } from "./types";

interface AuthContextType {
  user: UserResponse | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, displayName: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchUser = useCallback(async () => {
    try {
      const userData = await apiFetch<UserResponse>("/api/users/me");
      setUser(userData);
    } catch {
      setUser(null);
      setAccessToken(null);
    }
  }, []);

  // On mount, try to restore session from refresh token cookie
  useEffect(() => {
    async function init() {
      try {
        const res = await fetch("/api/auth/refresh", {
          method: "POST",
          credentials: "include",
        });
        if (res.ok) {
          const data: AuthResponse = await res.json();
          setAccessToken(data.accessToken);
          await fetchUser();
        }
      } catch {
        // No valid session
      } finally {
        setIsLoading(false);
      }
    }
    init();
  }, [fetchUser]);

  const login = useCallback(
    async (email: string, password: string) => {
      const data = await apiFetch<AuthResponse>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      setAccessToken(data.accessToken);
      await fetchUser();
    },
    [fetchUser]
  );

  const register = useCallback(
    async (email: string, password: string, displayName: string) => {
      const data = await apiFetch<AuthResponse>("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ email, password, displayName }),
      });
      setAccessToken(data.accessToken);
      await fetchUser();
    },
    [fetchUser]
  );

  const logout = useCallback(async () => {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch {
      // Best effort
    }
    setAccessToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}
```

---

## Task 18: Login & Register Pages

**Files:**
- Create: `web/src/app/login/page.tsx`
- Create: `web/src/app/register/page.tsx`

- [ ] **Step 1: Create login page**

Create `web/src/app/login/page.tsx`:

```tsx
"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";

export default function LoginPage() {
  const { login } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      await login(email, password);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.error || "Login failed");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <h1 className="text-2xl font-semibold tracking-tight text-center mb-8">
          Sign in to Reflect
        </h1>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              id="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              placeholder="••••••••"
            />
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isSubmitting ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-gray-500">
          Don&apos;t have an account?{" "}
          <Link href="/register" className="text-primary-600 hover:text-primary-700 font-medium">
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Create register page**

Create `web/src/app/register/page.tsx`:

```tsx
"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";

export default function RegisterPage() {
  const { register } = useAuth();
  const router = useRouter();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      await register(email, password, displayName);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.error || "Registration failed");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <h1 className="text-2xl font-semibold tracking-tight text-center mb-8">
          Create your account
        </h1>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="displayName" className="block text-sm font-medium text-gray-700 mb-1">
              Name
            </label>
            <input
              id="displayName"
              type="text"
              required
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              className="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              placeholder="Your name"
            />
          </div>

          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
              Email
            </label>
            <input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              id="password"
              type="password"
              required
              minLength={8}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
              placeholder="At least 8 characters"
            />
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isSubmitting ? "Creating account..." : "Create account"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-gray-500">
          Already have an account?{" "}
          <Link href="/login" className="text-primary-600 hover:text-primary-700 font-medium">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}
```

---

## Task 19: Header Component

**Files:**
- Create: `web/src/components/Header.tsx`

- [ ] **Step 1: Create Header component**

Create `web/src/components/Header.tsx`:

```tsx
"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";

export default function Header() {
  const { user, logout } = useAuth();
  const router = useRouter();

  if (!user) return null;

  async function handleLogout() {
    await logout();
    router.push("/login");
  }

  return (
    <header className="border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-14 max-w-3xl items-center justify-between px-4">
        <Link
          href="/check-in"
          className="text-lg font-semibold tracking-tight text-gray-900"
        >
          Reflect
        </Link>

        <nav className="flex items-center gap-6">
          <Link
            href="/history"
            className="text-sm text-gray-500 hover:text-gray-900"
          >
            History
          </Link>

          <div className="flex items-center gap-3">
            <span className="text-sm text-gray-500">{user.displayName}</span>
            <button
              onClick={handleLogout}
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              Sign out
            </button>
          </div>
        </nav>
      </div>
    </header>
  );
}
```

---

## Task 20: Check-In Wizard

**Files:**
- Create: `web/src/components/EnergySlider.tsx`
- Create: `web/src/components/CheckInWizard.tsx`

- [ ] **Step 1: Create EnergySlider component**

Create `web/src/components/EnergySlider.tsx`:

```tsx
"use client";

import { Slider } from "@ark-ui/react/slider";

interface EnergySliderProps {
  value: number;
  onChange: (value: number) => void;
}

const energyLabels: Record<number, string> = {
  1: "Exhausted",
  2: "Very low",
  3: "Low",
  4: "Below average",
  5: "Neutral",
  6: "Above average",
  7: "Good",
  8: "High",
  9: "Very high",
  10: "Peak",
};

function energyColor(value: number): string {
  if (value <= 3) return "bg-red-500";
  if (value <= 5) return "bg-amber-500";
  if (value <= 7) return "bg-emerald-400";
  return "bg-emerald-500";
}

export default function EnergySlider({ value, onChange }: EnergySliderProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <span className="text-4xl font-semibold text-gray-900">{value}</span>
        <span className="text-sm text-gray-500">{energyLabels[value]}</span>
      </div>

      <Slider.Root
        min={1}
        max={10}
        step={1}
        value={[value]}
        onValueChange={(details) => onChange(details.value[0])}
      >
        <Slider.Control className="relative flex items-center h-6">
          <Slider.Track className="relative h-2 w-full rounded-full bg-gray-200">
            <Slider.Range className={`absolute h-full rounded-full ${energyColor(value)}`} />
          </Slider.Track>
          <Slider.Thumb
            index={0}
            className="block h-5 w-5 rounded-full border-2 border-primary-600 bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 cursor-grab active:cursor-grabbing"
          />
        </Slider.Control>
      </Slider.Root>

      <div className="flex justify-between text-xs text-gray-400">
        <span>1</span>
        <span>5</span>
        <span>10</span>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Create CheckInWizard component**

Create `web/src/components/CheckInWizard.tsx`:

```tsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, CheckInRequest } from "@/lib/types";
import EnergySlider from "./EnergySlider";

interface WizardProps {
  existing?: CheckInResponse | null;
}

const STEPS = [
  { key: "wins", label: "Wins", prompt: "What moved forward this week?" },
  { key: "friction", label: "Friction", prompt: "Where did you feel resistance?" },
  { key: "energyRating", label: "Energy", prompt: "How was your energy this week?" },
  { key: "signalMoment", label: "Signal Moment", prompt: "What interaction or moment is still on your mind?" },
  { key: "intentions", label: "Intentions", prompt: "What matters most next week?" },
] as const;

export default function CheckInWizard({ existing }: WizardProps) {
  const router = useRouter();
  const [step, setStep] = useState(0);
  const [checkInId, setCheckInId] = useState<string | null>(existing?.id ?? null);
  const [isSaving, setIsSaving] = useState(false);

  const [wins, setWins] = useState(existing?.wins ?? "");
  const [friction, setFriction] = useState(existing?.friction ?? "");
  const [energyRating, setEnergyRating] = useState(existing?.energyRating ?? 5);
  const [signalMoment, setSignalMoment] = useState(existing?.signalMoment ?? "");
  const [intentions, setIntentions] = useState(existing?.intentions ?? "");

  function getCurrentValue(): string | number {
    switch (step) {
      case 0: return wins;
      case 1: return friction;
      case 2: return energyRating;
      case 3: return signalMoment;
      case 4: return intentions;
      default: return "";
    }
  }

  function buildRequest(): CheckInRequest {
    return {
      wins: wins || undefined,
      friction: friction || undefined,
      energyRating: energyRating,
      signalMoment: signalMoment || undefined,
      intentions: intentions || undefined,
      completed: step === STEPS.length - 1 ? true : undefined,
    };
  }

  async function saveProgress() {
    setIsSaving(true);
    try {
      const request = buildRequest();
      if (!checkInId) {
        const created = await apiFetch<CheckInResponse>("/api/check-ins", {
          method: "POST",
          body: JSON.stringify(request),
        });
        setCheckInId(created.id);
      } else {
        await apiFetch<CheckInResponse>(`/api/check-ins/${checkInId}`, {
          method: "PUT",
          body: JSON.stringify(request),
        });
      }
    } catch {
      // Save failed — user can retry
    } finally {
      setIsSaving(false);
    }
  }

  async function handleNext() {
    await saveProgress();
    if (step < STEPS.length - 1) {
      setStep(step + 1);
    } else {
      router.push("/check-in");
      router.refresh();
    }
  }

  function handleBack() {
    if (step > 0) setStep(step - 1);
  }

  const currentStep = STEPS[step];
  const isLastStep = step === STEPS.length - 1;
  const progress = ((step + 1) / STEPS.length) * 100;

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      {/* Progress bar */}
      <div className="mb-8">
        <div className="flex justify-between text-xs text-gray-400 mb-2">
          <span>Step {step + 1} of {STEPS.length}</span>
          <span>{currentStep.label}</span>
        </div>
        <div className="h-1.5 w-full rounded-full bg-gray-100">
          <div
            className="h-full rounded-full bg-primary-600 transition-all duration-300"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      {/* Prompt */}
      <h2 className="text-xl font-semibold text-gray-900 mb-6">
        {currentStep.prompt}
      </h2>

      {/* Input */}
      <div className="mb-8">
        {step === 2 ? (
          <EnergySlider
            value={energyRating}
            onChange={setEnergyRating}
          />
        ) : (
          <textarea
            value={getCurrentValue() as string}
            onChange={(e) => {
              const val = e.target.value;
              switch (step) {
                case 0: setWins(val); break;
                case 1: setFriction(val); break;
                case 3: setSignalMoment(val); break;
                case 4: setIntentions(val); break;
              }
            }}
            rows={6}
            className="w-full rounded-lg border border-gray-200 px-4 py-3 text-sm shadow-sm placeholder:text-gray-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500 resize-none"
            placeholder="Take your time..."
          />
        )}
      </div>

      {/* Navigation */}
      <div className="flex justify-between">
        {step > 0 ? (
          <button
            onClick={handleBack}
            className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50"
          >
            Back
          </button>
        ) : (
          <div />
        )}

        <button
          onClick={handleNext}
          disabled={isSaving}
          className="rounded-lg bg-primary-600 px-6 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSaving ? "Saving..." : isLastStep ? "Complete" : "Next"}
        </button>
      </div>
    </div>
  );
}
```

---

## Task 21: Check-In Display Components

**Files:**
- Create: `web/src/components/CheckInCard.tsx`
- Create: `web/src/components/CheckInDetail.tsx`

- [ ] **Step 1: Create CheckInCard component**

Create `web/src/components/CheckInCard.tsx`:

```tsx
import Link from "next/link";
import type { CheckInResponse } from "@/lib/types";

interface CheckInCardProps {
  checkIn: CheckInResponse;
}

function energyBadgeColor(rating: number | null): string {
  if (!rating) return "bg-gray-100 text-gray-500";
  if (rating <= 3) return "bg-red-100 text-red-700";
  if (rating <= 5) return "bg-amber-100 text-amber-700";
  if (rating <= 7) return "bg-emerald-100 text-emerald-700";
  return "bg-emerald-200 text-emerald-800";
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", {
    month: "long",
    day: "numeric",
    year: "numeric",
  })}`;
}

export default function CheckInCard({ checkIn }: CheckInCardProps) {
  const preview = checkIn.wins
    ? checkIn.wins.length > 100
      ? checkIn.wins.substring(0, 100) + "..."
      : checkIn.wins
    : "No wins recorded";

  return (
    <Link href={`/history/${checkIn.id}`}>
      <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-900">
            {formatWeekDate(checkIn.weekStart)}
          </span>
          <div className="flex items-center gap-2">
            {!checkIn.completed && (
              <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs text-amber-700">
                In progress
              </span>
            )}
            {checkIn.energyRating && (
              <span
                className={`rounded-full px-2 py-0.5 text-xs font-medium ${energyBadgeColor(checkIn.energyRating)}`}
              >
                Energy: {checkIn.energyRating}
              </span>
            )}
          </div>
        </div>
        <p className="text-sm text-gray-500 line-clamp-2">{preview}</p>
      </div>
    </Link>
  );
}
```

- [ ] **Step 2: Create CheckInDetail component**

Create `web/src/components/CheckInDetail.tsx`:

```tsx
import type { CheckInResponse } from "@/lib/types";
import Link from "next/link";

interface CheckInDetailProps {
  checkIn: CheckInResponse;
}

function Section({ label, content }: { label: string; content: string | null }) {
  if (!content) return null;
  return (
    <div className="space-y-1">
      <h3 className="text-xs font-medium uppercase tracking-wide text-gray-400">
        {label}
      </h3>
      <p className="text-sm text-gray-700 whitespace-pre-wrap">{content}</p>
    </div>
  );
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", {
    month: "long",
    day: "numeric",
    year: "numeric",
  })}`;
}

export default function CheckInDetail({ checkIn }: CheckInDetailProps) {
  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-xl font-semibold text-gray-900">
          {formatWeekDate(checkIn.weekStart)}
        </h1>
        {checkIn.energyRating && (
          <span className="text-sm text-gray-500">
            Energy: {checkIn.energyRating}/10
          </span>
        )}
      </div>

      <div className="space-y-6 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <Section label="Wins" content={checkIn.wins} />
        <Section label="Friction" content={checkIn.friction} />
        <Section label="Signal Moment" content={checkIn.signalMoment} />
        <Section label="Intentions" content={checkIn.intentions} />
      </div>

      <div className="mt-6 text-center">
        <Link
          href="/history"
          className="text-sm text-primary-600 hover:text-primary-700 font-medium"
        >
          View all check-ins
        </Link>
      </div>
    </div>
  );
}
```

---

## Task 22: History Page

**Files:**
- Create: `web/src/app/history/page.tsx`
- Create: `web/src/app/history/[id]/page.tsx`

- [ ] **Step 1: Create history timeline page**

Create `web/src/app/history/page.tsx`:

```tsx
"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInCard from "@/components/CheckInCard";

export default function HistoryPage() {
  const [checkIns, setCheckIns] = useState<CheckInResponse[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(true);

  async function loadPage(pageNum: number) {
    setIsLoading(true);
    try {
      const data = await apiFetch<PaginatedResponse<CheckInResponse>>(
        `/api/check-ins?page=${pageNum}&size=10`
      );
      if (pageNum === 0) {
        setCheckIns(data.content);
      } else {
        setCheckIns((prev) => [...prev, ...data.content]);
      }
      setHasMore(!data.last);
    } catch {
      // Failed to load
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadPage(0);
  }, []);

  function handleLoadMore() {
    const nextPage = page + 1;
    setPage(nextPage);
    loadPage(nextPage);
  }

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <h1 className="text-xl font-semibold text-gray-900 mb-6">Your check-ins</h1>

      {isLoading && checkIns.length === 0 ? (
        <p className="text-sm text-gray-500">Loading...</p>
      ) : checkIns.length === 0 ? (
        <p className="text-sm text-gray-500">No check-ins yet. Start your first one!</p>
      ) : (
        <div className="space-y-3">
          {checkIns.map((checkIn) => (
            <CheckInCard key={checkIn.id} checkIn={checkIn} />
          ))}

          {hasMore && (
            <button
              onClick={handleLoadMore}
              disabled={isLoading}
              className="w-full rounded-lg border border-gray-200 py-2 text-sm text-gray-500 hover:bg-gray-50 disabled:opacity-50"
            >
              {isLoading ? "Loading..." : "Load more"}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 2: Create single check-in detail page**

Create `web/src/app/history/[id]/page.tsx`:

```tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInDetail from "@/components/CheckInDetail";

export default function CheckInDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [checkIn, setCheckIn] = useState<CheckInResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const data = await apiFetch<CheckInResponse>(`/api/check-ins/${id}`);
        setCheckIn(data);
      } catch {
        // Not found
      }
      setIsLoading(false);
    }
    load();
  }, [id]);

  if (isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <p className="text-sm text-gray-500">Loading...</p>
      </div>
    );
  }

  if (!checkIn) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <p className="text-sm text-gray-500">Check-in not found.</p>
      </div>
    );
  }

  return <CheckInDetail checkIn={checkIn} />;
}

---

## Task 23: Landing Page & Routing

**Files:**
- Create: `web/src/app/page.tsx`
- Create: `web/src/app/check-in/page.tsx`
- Create: `web/src/app/check-in/new/page.tsx`
- Modify: `web/src/app/layout.tsx`

- [ ] **Step 1: Create root redirect page**

Create `web/src/app/page.tsx`:

```tsx
"use client";

import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function RootPage() {
  const { user, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading) {
      router.replace(user ? "/check-in" : "/login");
    }
  }, [user, isLoading, router]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-sm text-gray-400">Loading...</p>
    </div>
  );
}
```

- [ ] **Step 2: Create check-in landing page**

Create `web/src/app/check-in/page.tsx`:

```tsx
"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInWizard from "@/components/CheckInWizard";
import CheckInDetail from "@/components/CheckInDetail";

export default function CheckInPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [checkIn, setCheckIn] = useState<CheckInResponse | null>(null);
  const [notFound, setNotFound] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) {
      router.replace("/login");
      return;
    }

    if (!authLoading && user) {
      apiFetch<CheckInResponse>("/api/check-ins/current")
        .then((data) => {
          setCheckIn(data);
          setIsLoading(false);
        })
        .catch(() => {
          setNotFound(true);
          setIsLoading(false);
        });
    }
  }, [authLoading, user, router]);

  if (authLoading || isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <p className="text-sm text-gray-400">Loading...</p>
      </div>
    );
  }

  // No check-in this week → show wizard
  if (notFound) {
    return <CheckInWizard />;
  }

  // Incomplete check-in → resume wizard
  if (checkIn && !checkIn.completed) {
    return <CheckInWizard existing={checkIn} />;
  }

  // Completed → show detail
  if (checkIn && checkIn.completed) {
    return <CheckInDetail checkIn={checkIn} />;
  }

  return <CheckInWizard />;
}
```

- [ ] **Step 3: Create force-new wizard page**

Create `web/src/app/check-in/new/page.tsx`:

```tsx
"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInWizard from "@/components/CheckInWizard";

export default function NewCheckInPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [existing, setExisting] = useState<CheckInResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) {
      router.replace("/login");
      return;
    }

    if (!authLoading && user) {
      apiFetch<CheckInResponse>("/api/check-ins/current")
        .then((data) => {
          setExisting(data);
          setIsLoading(false);
        })
        .catch(() => {
          setIsLoading(false);
        });
    }
  }, [authLoading, user, router]);

  if (authLoading || isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <p className="text-sm text-gray-400">Loading...</p>
      </div>
    );
  }

  return <CheckInWizard existing={existing} />;
}
```

- [ ] **Step 4: Update root layout with AuthProvider and Header**

Replace `web/src/app/layout.tsx` with:

```tsx
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { AuthProvider } from "@/lib/auth";
import Header from "@/components/Header";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Reflect",
  description: "Guided weekly review for working professionals",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          <Header />
          <main>{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
```

---

## Task 24: End-to-End Local Verification

**Files:**
- Verify: all backend and frontend files
- Modify: `docker-compose.yml` (already exists at root — no changes needed)

- [ ] **Step 1: Start local infrastructure**

```bash
docker-compose up -d
```

Expected: Three containers running — `reflect-postgres`, `reflect-redis`, `reflect-mailhog`.

- [ ] **Step 2: Generate RSA keys for local development**

```bash
cd api
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
```

Set the keys as environment variables before starting the backend. Create a `.env.local` file or export them:

```bash
export JWT_PRIVATE_KEY="$(cat private.pem)"
export JWT_PUBLIC_KEY="$(cat public.pem)"
```

- [ ] **Step 3: Run the backend**

```bash
cd api && mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Expected: Application starts on port 8080. Flyway runs V1, V2, V3 migrations. Health check at `http://localhost:8080/actuator/health` returns `{"status":"UP"}`.

- [ ] **Step 4: Run the frontend**

In a separate terminal:

```bash
cd web && npm run dev
```

Expected: Next.js starts on port 3000. Navigate to `http://localhost:3000` — should redirect to `/login`.

- [ ] **Step 5: Manual smoke test**

1. Open `http://localhost:3000/register` — create an account
2. Should redirect to `/check-in` — wizard appears
3. Fill in Step 1 (Wins), click Next
4. Fill in Step 2 (Friction), click Next
5. Adjust Step 3 (Energy slider), click Next
6. Fill in Step 4 (Signal Moment), click Next
7. Fill in Step 5 (Intentions), click Complete
8. Should show the completed check-in detail
9. Click "History" in the header — timeline shows the entry
10. Click "Sign out" — returns to login
11. Log back in — should show the completed check-in (not the wizard)

- [ ] **Step 6: Run full backend test suite**

```bash
cd api && mvn verify
```

Expected: BUILD SUCCESS with all unit and integration tests passing.

- [ ] **Step 7: Run frontend build check**

```bash
cd web && npm run build
```

Expected: Build succeeds with no TypeScript or lint errors.

---

## Task 25: Initial Git Commit

Only proceed once Tasks 1-24 are all verified.

- [ ] **Step 1: Verify all files are in place**

```bash
ls api/src/main/java/com/reflect/config/
ls api/src/main/java/com/reflect/domain/
ls api/src/main/java/com/reflect/repository/
ls api/src/main/java/com/reflect/service/
ls api/src/main/java/com/reflect/controller/dto/
ls api/src/main/java/com/reflect/exception/
ls api/src/main/resources/db/migration/
ls web/src/app/
ls web/src/components/
ls web/src/lib/
```

- [ ] **Step 2: Add all files and create initial commit**

```bash
git add .
git commit -m "feat: initial MVP — auth + check-in (Spring Boot + Next.js)"
```

- [ ] **Step 3: Push to GitHub**

```bash
git branch -M main
git remote add origin git@github.com:jeffjordan97/Reflect.git
git push -u origin main
```
