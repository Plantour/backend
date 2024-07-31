package com.qnelldo.plantour.quest.dto;

import com.qnelldo.plantour.common.enums.Season;
import lombok.Data;

@Data
public class QuestDto {
    private Long id;
    private String name;
    private String description;
    private Season season;
    private int completedPuzzles;
}