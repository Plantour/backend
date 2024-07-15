package com.qnelldo.plantour.mission.entity;

import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mission_completions")
public class MissionCompletionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private MissionEntity mission;

    /**
     * puzzleNumber는 1부터 9까지의 값을 가집니다.
     * 이는 9개로 나뉜 퍼즐 중 어떤 조각을 완성했는지를 나타냅니다.
     */
    @Column(nullable = false)
    private int puzzleNumber;

    @ManyToOne
    @JoinColumn(name = "plant_id", nullable = false)
    private PlantEntity plant;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Double latitude;
    private Double longitude;
    private LocalDateTime completedAt;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image; // BASE64 인코딩된 이미지
}