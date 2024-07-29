package com.qnelldo.plantour.mission.service;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.repository.MissionRepository;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private MissionRepository missionRepository;


//    public MissionEntity createMissionForSeason(Season season) {
//        List<PlantEntity> seasonPlants = plantRepository.findAllBySeason(season);
//
//        if (seasonPlants.size() < 9) {
//            throw new IllegalStateException("Not enough plants for season: " + season);
//        }
//
//        MissionEntity mission = new MissionEntity();
//        mission.setName(season.name() + " Mission");
//        mission.setSeason(season);
//
//        // 해당 계절의 식물 중 랜덤으로 9개 선택
//        Collections.shuffle(seasonPlants);
//        List<PlantEntity> selectedPlants = seasonPlants.subList(0, 9);
//
//        for (int i = 0; i < 9; i++) {
//            mission.addPlant(selectedPlants.get(i), i + 1);
//        }
//
//        return missionRepository.save(mission);
//    }

//    public void createAllSeasonMissions() {
//        for (Season season : Season.values()) {
//            createMissionForSeason(season);
//        }
//    }

    public MissionEntity getCurrentSeasonMission() {
        Season currentSeason = getCurrentSeason();
        return getMissionBySeason(currentSeason);
    }

    public MissionEntity getMissionBySeason(Season season) {
        return missionRepository.findBySeason(season)
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다: " + season));
    }

    public MissionEntity getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다: " + id));
    }

    public Season getCurrentSeason() {
        return Season.fromMonth(LocalDate.now().getMonthValue());
    }

    public Map<String, Object> getMissionFlowersBySeason(Season season, String languageCode) {
        MissionEntity mission = getMissionBySeason(season);
        List<Map<String, Object>> flowers = mission.getMissionPlants().stream()
                .map(missionPlant -> {
                    PlantEntity plant = missionPlant.getPlant();
                    Map<String, Object> flowerMap = new HashMap<>();
                    flowerMap.put("flowerId", plant.getId());
                    flowerMap.put("flowerName", plant.getName().get(languageCode));
                    flowerMap.put("imgUrl", plant.getImageUrl());
                    flowerMap.put("characteristics", Arrays.asList(
                            plant.getCharacteristics1().get(languageCode),
                            plant.getCharacteristics2().get(languageCode),
                            plant.getCharacteristics3().get(languageCode)
                    ));
                    return flowerMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("season", season.name());
        result.put("flowers", flowers);
        return result;
    }
}