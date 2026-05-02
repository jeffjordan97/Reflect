package com.reflect.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 200)
    private String profession;

    @Column(length = 100)
    private String industry;

    @Column(name = "role_level", length = 50)
    private String roleLevel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "focus_areas", columnDefinition = "text[]")
    private String[] focusAreas;

    @Column(name = "bio_context", columnDefinition = "TEXT")
    private String bioContext;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
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
    public String getProfession() { return profession; }
    public String getIndustry() { return industry; }
    public String getRoleLevel() { return roleLevel; }
    public String[] getFocusAreas() { return focusAreas; }
    public String getBioContext() { return bioContext; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setProfession(String profession) { this.profession = profession; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setRoleLevel(String roleLevel) { this.roleLevel = roleLevel; }
    public void setFocusAreas(String[] focusAreas) { this.focusAreas = focusAreas; }
    public void setBioContext(String bioContext) { this.bioContext = bioContext; }
}
