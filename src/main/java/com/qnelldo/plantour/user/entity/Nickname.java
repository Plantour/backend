package com.qnelldo.plantour.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "nicknames")
public class Nickname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String koreanName;

    @Column(nullable = false)
    private String englishName;

    @Column(nullable = false)
    private Integer usageCount = 0;

    public void incrementUsageCount() {
        this.usageCount++;
    }
}
