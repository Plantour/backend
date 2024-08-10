package com.qnelldo.plantour.quest.dto;

import com.qnelldo.plantour.plant.dto.PlantDTO;
import lombok.Data;

@Data
public class QuestPlantDTO {
    private Long id;
    private PlantDTO plant;
}