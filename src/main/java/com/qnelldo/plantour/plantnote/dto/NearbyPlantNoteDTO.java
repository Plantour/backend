package com.qnelldo.plantour.plantnote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NearbyPlantNoteDTO {
    private Long id;
    private String title;
    private String content;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    // 사용자 정보
    private String imageUrl;
    private Long userId;
    private String userName;

    // 식물 정보
    private String infoType;  // plantInfoType 대신
    private Long plantId;     // selectedPlantId 대신
    private String plantInfo; // customPlantInfo 대신
}