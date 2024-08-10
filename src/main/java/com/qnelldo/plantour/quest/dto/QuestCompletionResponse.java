package com.qnelldo.plantour.quest.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestCompletionResponse {
    private QuestDTO quest;
    private Map<String, Object> plantData;
    private List<QuestCompletionDTO> completedQuests;

    public QuestCompletionResponse(QuestDTO quest, Map<String, Object> plantData, List<QuestCompletionDTO> completedQuests) {
        this.quest = quest;
        this.plantData = plantData;
        this.completedQuests = completedQuests;
    }
}
