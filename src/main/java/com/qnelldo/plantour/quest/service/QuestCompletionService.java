package com.qnelldo.plantour.quest.service;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.quest.repository.QuestCompletionRepository;
import com.qnelldo.plantour.quest.repository.QuestRepository;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestCompletionService {

    private final UserRepository userRepository;
    private final QuestCompletionRepository questCompletionRepository;
    private final QuestRepository questRepository;
    private final PlantRepository plantRepository;

    @Autowired
    public QuestCompletionService(UserRepository userRepository,
                                  QuestCompletionRepository questCompletionRepository,
                                  QuestRepository questRepository,
                                  PlantRepository plantRepository) {
        this.userRepository = userRepository;
        this.questCompletionRepository = questCompletionRepository;
        this.questRepository = questRepository;
        this.plantRepository = plantRepository;
    }


    public List<QuestCompletionEntity> getCompletedQuestsBySeason(Long userId, Season season) {
        return questCompletionRepository.findByUserIdAndQuestSeason(userId, season);
    }

    public QuestCompletionEntity getQuestCompletionById(Long id) {
        return questCompletionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("완료된 퀘스트를 찾을 수 없습니다"));
    }


    public QuestCompletionEntity completeQuestPuzzle(
            Long userId, Long questId, int puzzleNumber, Long plantId,
            String content, byte[] imageData, Double latitude, Double longitude) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        QuestEntity quest = questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("퀘스트를 찾을 수 없습니다."));
        PlantEntity plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));

        return questCompletionRepository.findByUserAndQuestAndPlantAndPuzzleNumber(user, quest, plant, puzzleNumber)
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
    }

}