package com.qnelldo.plantour.mission.service;

import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    @Autowired
    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public MissionEntity createMission(MissionEntity mission) {
        return missionRepository.save(mission);
    }

    public Optional<MissionEntity> getMissionById(Long id) {
        return missionRepository.findById(id);
    }

    public List<MissionEntity> getAllMissions() {
        return missionRepository.findAll();
    }

    public MissionEntity updateMission(MissionEntity mission) {
        return missionRepository.save(mission);
    }

    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    public List<MissionEntity> findMissionsNearby(double latitude, double longitude, double distanceInKm) {
        return missionRepository.findMissionsWithinDistance(latitude, longitude, distanceInKm);
    }

    public List<MissionEntity> findMissionsByPlant(Long plantId) {
        return missionRepository.findMissionsByPlantId(plantId);
    }
}
