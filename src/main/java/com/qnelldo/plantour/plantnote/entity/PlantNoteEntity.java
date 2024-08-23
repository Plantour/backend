package com.qnelldo.plantour.plantnote.entity;

import com.qnelldo.plantour.plantnote.enums.PlantInfoType;
import com.qnelldo.plantour.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "plant_notes")
@Getter
@Setter
public class PlantNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(name = "image_data", columnDefinition="LONGBLOB")
    private byte[] imageData;

    @Enumerated(EnumType.STRING)

    private PlantInfoType plantInfoType;

    private Long selectedPlantId;

    private String customPlantInfo;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}