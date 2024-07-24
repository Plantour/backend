package com.qnelldo.plantour.mission.controller;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.dto.MissionCompletionRequest;
import com.qnelldo.plantour.mission.dto.UserMissionProgressDto;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.service.MissionService;
import com.qnelldo.plantour.mission.entity.MissionCompletionEntity;
import com.qnelldo.plantour.mission.service.MissionCompletionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "미션 컨트롤러", description = "미션 관리 및 완료 처리")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://4274-218-233-42-240.ngrok-free.app"}, maxAge = 3600)
public class MissionController {
    private static final Logger logger = LoggerFactory.getLogger(MissionController.class);

    private final MissionService missionService;
    private final MissionCompletionService missionCompletionService;

    @Autowired
    public MissionController(MissionService missionService, MissionCompletionService missionCompletionService) {
        this.missionService = missionService;
        this.missionCompletionService = missionCompletionService;
    }

    @GetMapping("/current")
    public ResponseEntity<MissionEntity> getCurrentSeasonMission() {
        logger.info("Fetching current season mission");
        return ResponseEntity.ok(missionService.getCurrentSeasonMission());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<MissionEntity> getMissionBySeason(@RequestParam(required = false) Season season) {
        logger.info("Fetching mission for season: {}", season);
        if (season == null) {
            season = missionService.getCurrentSeason();
        }
        return ResponseEntity.ok(missionService.getMissionBySeason(season));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MissionEntity> getMissionById(@PathVariable Long id) {
        logger.info("Fetching mission with id: {}", id);
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/progress/{userId}")
    public ResponseEntity<UserMissionProgressDto> getUserMissionProgress(@PathVariable Long userId) {
        logger.info("Fetching mission progress for user: {}", userId);
        UserMissionProgressDto progress = missionCompletionService.getUserMissionProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/complete")
    public ResponseEntity<?> completeMissionPuzzle(@RequestBody MissionCompletionRequest request) {
        logger.info("Completing mission puzzle for user: {}, mission: {}", request.getUserId(), request.getMissionId());
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
            logger.error("Error completing mission puzzle", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/flowers")
    public ResponseEntity<Map<String, Object>> getMissionFlowers(
            @RequestParam(required = false) Season season,
            @RequestParam(defaultValue = "ENG") String languageCode) {
        logger.info("Received request for mission flowers");
        if (season == null) {
            logger.info("Season is null, setting to current season");
            season = missionService.getCurrentSeason();
        }
        logger.info("Fetching mission flowers for season: {} in language: {}", season, languageCode);
        Map<String, Object> flowerData = missionService.getMissionFlowersBySeason(season, languageCode);
        logger.info("Returning flower data");
        return ResponseEntity.ok(flowerData);
    }
}