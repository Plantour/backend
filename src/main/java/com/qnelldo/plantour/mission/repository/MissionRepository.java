package com.qnelldo.plantour.mission.repository;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    List<MissionEntity> findBySeason(Season season);
}