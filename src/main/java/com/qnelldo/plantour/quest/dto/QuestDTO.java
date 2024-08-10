package com.qnelldo.plantour.quest.dto;

import com.qnelldo.plantour.common.enums.Season;
import lombok.Data;

import java.util.List;

@Data
public class QuestDTO {
    private Long id;
    private String name;
    private Season season;
    private List<QuestPlantDTO> questPlants;
}