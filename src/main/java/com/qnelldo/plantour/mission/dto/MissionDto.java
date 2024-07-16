package com.qnelldo.plantour.mission.dto;

import com.qnelldo.plantour.enums.Season;
import lombok.Data;

@Data
public class MissionDto {
    private Long id;
    private String name;
    private String description;
    private Season season;
    private int completedPuzzles;
}