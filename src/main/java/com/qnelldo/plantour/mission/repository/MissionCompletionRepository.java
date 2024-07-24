package com.qnelldo.plantour.mission.repository;

import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.entity.MissionCompletionEntity;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionCompletionRepository extends JpaRepository<MissionCompletionEntity, Long> {

    boolean existsByUserAndMissionAndPlant(UserEntity user, MissionEntity mission, PlantEntity plant);
    List<MissionCompletionEntity> findByUserAndMission(UserEntity user, MissionEntity mission);
    boolean existsByUserAndMissionAndPuzzleNumber(UserEntity user, MissionEntity mission, int puzzleNumber);

}
