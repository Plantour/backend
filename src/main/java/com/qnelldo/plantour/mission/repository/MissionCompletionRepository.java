package com.qnelldo.plantour.mission.repository;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.entity.MissionCompletionEntity;
import com.qnelldo.plantour.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionCompletionRepository extends JpaRepository<MissionCompletionEntity, Long> {

    List<MissionCompletionEntity> findByUserIdAndMissionSeason(Long userId, Season season);
    boolean existsByUserAndMissionAndPuzzleNumber(UserEntity user, MissionEntity mission, int puzzleNumber);
}
