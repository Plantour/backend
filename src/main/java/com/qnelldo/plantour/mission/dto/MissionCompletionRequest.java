package com.qnelldo.plantour.mission.dto;

import lombok.Data;

@Data
public class MissionCompletionRequest {
    private Long userId;
    private Long missionId;
    private int puzzleNumber;
    private Long plantId;
    private String content;
    private String image;
    private Double latitude;
    private Double longitude;
}