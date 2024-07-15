package com.qnelldo.plantour.mission.controller;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.dto.MissionCompletionRequest;
import com.qnelldo.plantour.mission.dto.UserMissionProgressDto;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.service.MissionService;
import com.qnelldo.plantour.mission.entity.MissionCompletionEntity;
import com.qnelldo.plantour.mission.service.MissionCompletionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "미션 컨트롤러", description = "미션 관리 및 완료 처리")
public class MissionController {

    private final MissionService missionService;
    private final MissionCompletionService missionCompletionService;

    @Autowired
    public MissionController(MissionService missionService, MissionCompletionService missionCompletionService) {
        this.missionService = missionService;
        this.missionCompletionService = missionCompletionService;
    }

    @GetMapping("/current")
    public ResponseEntity<List<MissionEntity>> getCurrentSeasonMissions() {
        return ResponseEntity.ok(missionService.getCurrentSeasonMissions());
    }

    @GetMapping
    public ResponseEntity<List<MissionEntity>> getMissionsBySeason(@RequestParam(required = false) Season season) {
        if (season == null) {
            season = missionService.getCurrentSeason();
        }
        return ResponseEntity.ok(missionService.getMissionsBySeason(season));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionEntity> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @GetMapping("/progress/{userId}")
    public ResponseEntity<UserMissionProgressDto> getUserMissionProgress(@PathVariable Long userId) {
        UserMissionProgressDto progress = missionCompletionService.getUserMissionProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeMissionPuzzle(@RequestBody MissionCompletionRequest request) {
        try {
            MissionCompletionEntity completedMission = missionCompletionService.completeMissionPuzzle(
                    request.getUserId(),
                    request.getMissionId(),
                    request.getPuzzleNumber(),
                    request.getPlantId(),
                    request.getContent(),
                    request.getImage(),
                    request.getLatitude(),
                    request.getLongitude()
            );
            return ResponseEntity.ok(completedMission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}