package com.qnelldo.plantour.quest.service;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.quest.dto.NearbyQuestDTO;
import com.qnelldo.plantour.quest.dto.QuestCompletionResponse;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.quest.repository.QuestCompletionRepository;
import com.qnelldo.plantour.quest.repository.QuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final QuestCompletionRepository questCompletionRepository;
    private final QuestCompletionService questCompletionService;

    public QuestService(QuestRepository questRepository, QuestCompletionService questCompletionService, QuestCompletionRepository questCompletionRepository) {
        this.questCompletionRepository = questCompletionRepository;
        this.questCompletionService = questCompletionService;
        this.questRepository = questRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(QuestService.class);

    @Value("${spring.app.base-url}")
    private String baseUrl;


    public QuestEntity getQuestBySeason(Season season) {
        return questRepository.findBySeason(season)
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다: " + season));
    }

    public QuestEntity getQuestById(Long id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다: " + id));
    }

    public Season getCurrentSeason() {
        return Season.fromMonth(LocalDate.now().getMonthValue());
    }

    public QuestCompletionResponse getQuestDataBySeason(Season season, Long userId) {
        QuestEntity quest = questRepository.findBySeason(season)
                .orElseThrow(() -> new RuntimeException("해당 시즌의 퀘스트를 찾을 수 없습니다."));
        Map<String, Object> questData = new HashMap<>();
        questData.put("id", quest.getId());
        questData.put("name", quest.getName());
        questData.put("season", quest.getSeason());

        Map<String, Object> plantData = getQuestPlantsBySeason(season, "KOR"); // 언어 코드는 필요에 따라 조정
        List<Map<String, Object>> completedQuests = questCompletionService.getCompletedQuestsBySeason(userId, season)
                .stream()
                .map(questCompletion -> {
                    Map<String, Object> questCompletionData = new HashMap<>();
                    questCompletionData.put("id", questCompletion.getId());
                    questCompletionData.put("content", questCompletion.getContent());
                    questCompletionData.put("completedAt", questCompletion.getCompletedAt());
                    questCompletionData.put("latitude", questCompletion.getLatitude());
                    questCompletionData.put("longitude", questCompletion.getLongitude());
                    questCompletionData.put("puzzleNumber", questCompletion.getPuzzleNumber());
                    questCompletionData.put("plantId", questCompletion.getPlant().getId());
                    questCompletionData.put("imageData", baseUrl + "/api/quests/image/" + questCompletion.getId());

                    return questCompletionData;
                })
                .collect(Collectors.toList());

        return new QuestCompletionResponse(questData, plantData, completedQuests);
    }

    public Map<String, Object> getQuestPlantsBySeason(Season season, String languageCode) {
        QuestEntity mission = getQuestBySeason(season);
        List<Map<String, Object>> plants = mission.getQuestPlants().stream()
                .map(missionPlant -> {
                    Map<String, Object> plantMap = new HashMap<>();
                    plantMap.put("plantId", missionPlant.getPlant().getId());
                    plantMap.put("plantName", missionPlant.getPlant().getName().get(languageCode));
                    plantMap.put("imgUrl", missionPlant.getPlant().getImageUrl());
                    plantMap.put("characteristics", List.of(
                            missionPlant.getPlant().getCharacteristics1().get(languageCode),
                            missionPlant.getPlant().getCharacteristics2().get(languageCode),
                            missionPlant.getPlant().getCharacteristics3().get(languageCode)
                    ));
                    return plantMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("season", season.name());
        result.put("plants", plants);
        return result;
    }

    public Map<String, List<NearbyQuestDTO>> getNearbyQuests(double latitude, double longitude, double radiusKm) {
        List<QuestCompletionEntity> nearbyQuests =
                questCompletionRepository.findNearbyQuests(latitude, longitude, radiusKm);

        List<NearbyQuestDTO> questDTOs = nearbyQuests.stream()
                .map(quest -> new NearbyQuestDTO(
                        quest.getId(),
                        quest.getContent(),
                        quest.getLatitude(),
                        quest.getLongitude(),
                        quest.getCompletedAt(),
                        baseUrl + "/api/quests/image/" + quest.getId(),
                        quest.getUser().getId(),
                        quest.getUser().getName(),
                        quest.getPlant().getId(),
                        quest.getPlant().getName().get("ENG")
                ))
                .collect(Collectors.toList());

        return Collections.singletonMap("nearbyQuests", questDTOs);
    }
}

