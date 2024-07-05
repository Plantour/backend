package com.qnelldo.plantour.mission.controller;

import com.qnelldo.plantour.mission.dto.MissionDto;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    public ResponseEntity<MissionEntity> createMission(@RequestBody MissionEntity mission) {
        MissionEntity createdMission = missionService.createMission(mission);
        return ResponseEntity.ok(createdMission);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionEntity> getMission(@PathVariable Long id) {
        return missionService.getMissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MissionEntity>> getAllMissions() {
        List<MissionEntity> missions = missionService.getAllMissions();
        return ResponseEntity.ok(missions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionEntity> updateMission(@PathVariable Long id, @RequestBody MissionEntity mission) {
        mission.setId(id);
        MissionEntity updatedMission = missionService.updateMission(mission);
        return ResponseEntity.ok(updatedMission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<MissionDto>> getNearbyMissions(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radius) {
        List<MissionEntity> missions = missionService.findMissionsNearby(latitude, longitude, radius);
        List<MissionDto> missionDto = missions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(missionDto);
    }

    private MissionDto convertToDto(MissionEntity mission) {
        return new MissionDto(
                mission.getId(),
                mission.getName(),
                mission.getDescription(),
                mission.getLatitude(),
                mission.getLongitude()
                // 필요한 다른 필드들...
        );
    }

    @GetMapping("/by-plant/{plantId}")
    public ResponseEntity<List<MissionEntity>> getMissionsByPlant(@PathVariable Long plantId) {
        List<MissionEntity> missions = missionService.findMissionsByPlant(plantId);
        return ResponseEntity.ok(missions);
    }
}