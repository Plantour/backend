package com.qnelldo.plantour.quest.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.plant.dto.PlantDTO;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.quest.dto.*;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.quest.entity.QuestPlantEntity;
import com.qnelldo.plantour.quest.repository.QuestCompletionRepository;
import com.qnelldo.plantour.quest.repository.QuestRepository;
import com.qnelldo.plantour.user.service.NicknameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LanguageContext languageContext;
    private final NicknameService nicknameService;

    public QuestService(NicknameService nicknameService, QuestRepository questRepository, QuestCompletionService questCompletionService, QuestCompletionRepository questCompletionRepository, LanguageContext languageContext) {
        this.questCompletionRepository = questCompletionRepository;
        this.questCompletionService = questCompletionService;
        this.questRepository = questRepository;
        this.languageContext = languageContext;
        this.nicknameService = nicknameService;
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
        String languageCode = languageContext.getCurrentLanguage();
        Map<String, Object> plantData = getQuestPlantsBySeason(season, languageCode);
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
        dto.setPlant(convertToPlantDTO(questPlant.getPlant())); // 이 줄에서 에러 발생
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
                .map(quest -> {
                    // 중간 변수 사용
                    Long questId = quest.getId();
                    String content = quest.getContent();
                    Double questLatitude = quest.getLatitude();
                    Double questLongitude = quest.getLongitude();
                    LocalDateTime completedAt = quest.getCompletedAt();
                    String imageUrl = baseUrl + "/api/quests/image/" + questId;
                    Long userId = quest.getUser().getId();
                    String userName = quest.getUser().getName();

                    // 닉네임 관련 중간 변수
                    String localizedNickname = nicknameService.getLocalizedNickname(quest.getUser().getId());

                    // 식물 정보 추가
                    Long plantId = quest.getPlant().getId();
                    String plantName = quest.getPlant().getName().get(languageContext.getCurrentLanguage());

                    return new NearbyQuestDTO(
                            questId,
                            content,
                            questLatitude,
                            questLongitude,
                            completedAt,
                            imageUrl,
                            userId,
                            userName,
                            plantId,
                            plantName,
                            localizedNickname
                    );
                })
                .collect(Collectors.toList());

        return Collections.singletonMap("nearbyQuests", questDTOs);
    }

}