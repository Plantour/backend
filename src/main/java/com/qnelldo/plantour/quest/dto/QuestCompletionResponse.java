package com.qnelldo.plantour.quest.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestCompletionResponse {
    private Map<String, Object> quest;
    private Map<String, Object> plantData;
    private List<Map<String, Object>> completedQuests;

    public QuestCompletionResponse(Map<String, Object> quest, Map<String, Object> plantData, List<Map<String, Object>> completedQuests) {
        this.quest = quest;
        this.plantData = plantData;
        this.completedQuests = completedQuests;
    }
}
