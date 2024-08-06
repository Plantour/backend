package com.qnelldo.plantour.quest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.impl.PointImpl;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "quest_completions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestCompletionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private QuestEntity quest;

    @Column(nullable = false)
    private int puzzleNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private PlantEntity plant;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Double latitude;

    private Double longitude;

    private LocalDateTime completedAt;

    @Lob
    @Column(name = "image_data", columnDefinition="LONGBLOB")
    private byte[] imageData;

}
