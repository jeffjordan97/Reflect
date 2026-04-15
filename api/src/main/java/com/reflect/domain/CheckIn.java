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
