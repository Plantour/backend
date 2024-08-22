package com.qnelldo.plantour.quest.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.quest.dto.QuestCompletionDTO;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.quest.repository.QuestCompletionRepository;
import com.qnelldo.plantour.quest.repository.QuestRepository;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestCompletionService {

    private final UserRepository userRepository;
    private final QuestCompletionRepository questCompletionRepository;
    private final QuestRepository questRepository;
    private final PlantRepository plantRepository;
    private final LanguageContext languageContext;

    @Autowired
    public QuestCompletionService(UserRepository userRepository,
                                  QuestCompletionRepository questCompletionRepository,
                                  QuestRepository questRepository,
                                  PlantRepository plantRepository, LanguageContext languageContext) {
        this.userRepository = userRepository;
        this.questCompletionRepository = questCompletionRepository;
        this.questRepository = questRepository;
        this.plantRepository = plantRepository;
        this.languageContext = languageContext;
    }


    @Value("${spring.app.base-url}")
    private String baseUrl;

    public List<QuestCompletionDTO> getCompletedQuestsBySeason(Long userId, Season season) {
        return questCompletionRepository.findByUserIdAndQuestSeason(userId, season)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuestCompletionDTO getQuestCompletionById(Long id) {
        return convertToDTO(questCompletionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("완료된 퀘스트를 찾을 수 없습니다")));
    }

    public byte[] getQuestImageData(Long id) {
        QuestCompletionEntity questCompletion = questCompletionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("완료된 퀘스트를 찾을 수 없습니다"));
        return questCompletion.getImageData();
    }


    public QuestCompletionDTO completeQuestPuzzle(
            Long userId, Long questId, int puzzleNumber, Long plantId,
            String content, byte[] imageData, Double latitude, Double longitude) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        QuestEntity quest = questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("퀘스트를 찾을 수 없습니다."));
        PlantEntity plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));

        QuestCompletionEntity entity = questCompletionRepository.findByUserAndQuestAndPlantAndPuzzleNumber(user, quest, plant, puzzleNumber)
                .orElseGet(() -> {
                    QuestCompletionEntity newCompletion = new QuestCompletionEntity();
                    newCompletion.setUser(user);
                    newCompletion.setQuest(quest);
                    newCompletion.setPlant(plant);
                    newCompletion.setPuzzleNumber(puzzleNumber);
                    newCompletion.setContent(content);
                    newCompletion.setImageData(imageData);
                    newCompletion.setLatitude(latitude);
                    newCompletion.setLongitude(longitude);
                    newCompletion.setCompletedAt(LocalDateTime.now());
                    return questCompletionRepository.save(newCompletion);
                });

        return convertToDTO(entity);
    }

    private QuestCompletionDTO convertToDTO(QuestCompletionEntity entity) {
        QuestCompletionDTO dto = new QuestCompletionDTO();
        String languageCode = languageContext.getCurrentLanguage();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setQuestId(entity.getQuest().getId());
        dto.setPuzzleNumber(entity.getPuzzleNumber());
        dto.setPlantId(entity.getPlant().getId());
        dto.setPlantName(entity.getPlant().getName().get(languageCode));
        dto.setContent(entity.getContent());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setImageUrl(baseUrl + "/api/quests/image/" + entity.getId());
        return dto;
    }

}