package com.qnelldo.plantour.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NearbyQuestDTO {

    private Long id;
    private String content;
    private Double latitude;
    private Double longitude;
    private LocalDateTime completedAt;
    private String imageUrl;

    // 사용자 정보
    private Long userId;
    private String userName;

    // 식물 정보
    private Long plantId;
    private String plantName;
}