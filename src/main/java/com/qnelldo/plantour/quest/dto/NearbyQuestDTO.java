package com.qnelldo.plantour.quest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NearbyQuestDTO {
    private Long id;
    private String content;
    private Double latitude;
    private Double longitude;
    private LocalDateTime completedAt;
    private String imageUrl;
    private Long userId;
    private String userName;
    private Long plantId;
    private String plantName;  // 언어에 맞는 plantName을 제공
    private String nickname;   // 언어에 맞는 닉네임을 제공

    public NearbyQuestDTO(Long id, String content, Double latitude, Double longitude, LocalDateTime completedAt,
                          String imageUrl, Long userId, String userName, Long plantId, String plantName, String nickname) {
        this.id = id;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completedAt = completedAt;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.plantId = plantId;
        this.plantName = plantName;
        this.nickname = nickname;
    }
}
