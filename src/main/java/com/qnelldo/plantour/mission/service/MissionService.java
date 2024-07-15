package com.qnelldo.plantour.mission.service;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    @Autowired
    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public List<MissionEntity> getMissionsBySeason(Season season) {
        return missionRepository.findBySeason(season);
    }

    public MissionEntity getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));
    }

    public Season getCurrentSeason() {
        return Season.fromMonth(LocalDate.now().getMonthValue());
    }

    public List<MissionEntity> getCurrentSeasonMissions() {
        return getMissionsBySeason(getCurrentSeason());
    }
}
