package com.qnelldo.plantour.plant.entity;

import com.qnelldo.plantour.mission.entity.MissionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "plants")
public class PlantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Season bestSeason;

    @ElementCollection
    @CollectionTable(name = "plant_characteristics", joinColumns = @JoinColumn(name = "plant_id"))
    @Column(name = "characteristic")
    private List<String> characteristics;

    @ManyToMany(mappedBy = "plants")
    private List<MissionEntity> missions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER, ALL
    }

    // Getters and Setters

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters for all fields...
}