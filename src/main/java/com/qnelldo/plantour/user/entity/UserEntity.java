package com.qnelldo.plantour.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "language_code", nullable = false)
    private String languageCode = "KOR";

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestCompletionEntity> questCompletions = new ArrayList<>();

    // Enum for auth provider
    public enum AuthProvider {
        google
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to add a quest completion
    public void addQuestCompletion(QuestCompletionEntity questCompletion) {
        questCompletions.add(questCompletion);
        questCompletion.setUser(this);
    }

    // Helper method to remove a quest completion
    public void removeQuestCompletion(QuestCompletionEntity questCompletion) {
        questCompletions.remove(questCompletion);
        questCompletion.setUser(null);
    }
}
