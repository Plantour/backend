package com.qnelldo.plantour.quest.service;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.quest.dto.*;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.quest.entity.QuestPlantEntity;
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


    public Season getCurrentSeason() {
        return Season.fromMonth(LocalDate.now().getMonthValue());
    }

    public QuestCompletionResponse getQuestDataBySeason(Season season, Long userId) {
        QuestEntity quest = questRepository.findBySeason(season)
                .orElseThrow(() -> new RuntimeException("해당 시즌의 퀘스트를 찾을 수 없습니다."));

        QuestDTO questDto = convertToQuestDTO(quest);
        Map<String, Object> plantData = getQuestPlantsBySeason(season, "KOR");
        List<QuestCompletionDTO> completedQuests = questCompletionService.getCompletedQuestsBySeason(userId, season);

        return new QuestCompletionResponse(questDto, plantData, completedQuests);
    }

    private QuestDTO convertToQuestDTO(QuestEntity quest) {
        QuestDTO dto = new QuestDTO();
        dto.setId(quest.getId());
        dto.setName(quest.getName());
        dto.setSeason(quest.getSeason());
        dto.setQuestPlants(quest.getQuestPlants().stream()
                .map(this::convertToQuestPlantDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private QuestPlantDTO convertToQuestPlantDTO(QuestPlantEntity questPlant) {
        QuestPlantDTO dto = new QuestPlantDTO();
        dto.setId(questPlant.getId());
        dto.setPlant(convertToPlantDTO(questPlant.getPlant()));
        return dto;
    }

    private PlantDTO convertToPlantDTO(PlantEntity plant) {
        PlantDTO dto = new PlantDTO();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setImageUrl(plant.getImageUrl());
        dto.setCharacteristics1(plant.getCharacteristics1());
        dto.setCharacteristics2(plant.getCharacteristics2());
        dto.setCharacteristics3(plant.getCharacteristics3());
        dto.setSeason(plant.getSeason());
        return dto;
    }

    private QuestCompletionDTO convertToQuestCompletionDTO(QuestCompletionEntity entity) {
        QuestCompletionDTO dto = new QuestCompletionDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setQuestId(entity.getQuest().getId());
        dto.setPuzzleNumber(entity.getPuzzleNumber());
        dto.setPlantId(entity.getPlant().getId());
        dto.setContent(entity.getContent());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setImageUrl(baseUrl + "/api/quests/image/" + entity.getId());
        return dto;
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
                        quest.getPlant().getName().get("ENG"),
                        quest.getUser().getNickname().get("ENG")
                ))
                .collect(Collectors.toList());

        return Collections.singletonMap("nearbyQuests", questDTOs);
    }
}

