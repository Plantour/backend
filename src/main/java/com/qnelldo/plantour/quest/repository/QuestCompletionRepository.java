package com.qnelldo.plantour.quest.repository;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import com.qnelldo.plantour.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestCompletionRepository extends JpaRepository<QuestCompletionEntity, Long> {

    Optional<QuestCompletionEntity> findByUserAndQuestAndPlantAndPuzzleNumber(UserEntity user, QuestEntity quest, PlantEntity plant, int puzzleNumber);
    List<QuestCompletionEntity> findByUserIdAndQuestSeason(Long userId, Season season);
}
