package com.qnelldo.plantour.quest.dto;

import com.qnelldo.plantour.common.enums.Season;
import lombok.Data;

import java.util.List;

@Data
public class QuestCompletionResponse {
    private Long questId;
    private String questName;
    private String season; // Season을 String으로 변경
    private List<PlantInfo> plants;
    private List<QuestCompletionDTO> completedQuests;

    @Data
    public static class PlantInfo {
        private Long plantId;
        private String plantName;
        private String imageUrl;
        private List<String> characteristics;
    }
}