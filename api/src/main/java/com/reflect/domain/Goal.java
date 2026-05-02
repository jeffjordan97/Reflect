package com.reflect.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String horizon;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;

    protected Goal() {}

    public Goal(User user, String title, String horizon) {
        this.user = user;
        this.title = title;
        this.horizon = horizon;
        this.status = "ACTIVE";
        this.sortOrder = 0;
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

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public void complete() {
        this.status = "COMPLETED";
        this.completedAt = OffsetDateTime.now();
    }

    public void release() {
        this.status = "RELEASED";
        this.releasedAt = OffsetDateTime.now();
    }

    public void pause() {
        this.status = "PAUSED";
    }

    public void resume() {
        this.status = "ACTIVE";
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getHorizon() { return horizon; }
    public String getStatus() { return status; }
    public LocalDate getTargetDate() { return targetDate; }
    public int getSortOrder() { return sortOrder; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public OffsetDateTime getReleasedAt() { return releasedAt; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setHorizon(String horizon) { this.horizon = horizon; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
