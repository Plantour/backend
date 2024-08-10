package com.qnelldo.plantour.quest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestCompletionDTO {
    private Long id;
    private Long userId;
    private Long questId;
    private int puzzleNumber;
    private Long plantId;
    private String content;
    private Double latitude;
    private Double longitude;
    private LocalDateTime completedAt;
    private String imageUrl;
}