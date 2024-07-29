package com.qnelldo.plantour.mission.repository;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    Optional<MissionEntity> findBySeason(Season season);
}