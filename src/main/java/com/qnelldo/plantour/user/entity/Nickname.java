package com.qnelldo.plantour.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nicknames")
@Data
public class Nickname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "korean_name")
    private String koreanName;

    @Column(name = "usage_count")
    private int usageCount;

    public void incrementUsageCount() {
        this.usageCount++;
    }

    public String getLocalizedNickname(String languageCode) {
        return "KOR".equalsIgnoreCase(languageCode) ? koreanName : englishName;
    }
}
