package com.qnelldo.plantour.quest.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.quest.dto.QuestCompletionResponse;
import com.qnelldo.plantour.quest.entity.QuestCompletionEntity;
import com.qnelldo.plantour.quest.service.QuestCompletionService;
import com.qnelldo.plantour.quest.service.QuestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@Tag(name = "퀘스트 컨트롤러", description = "퀘스트 관리 및 완료 처리")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://f940-218-233-42-240.ngrok-free.app"}, maxAge = 3600)
public class QuestController {
    private static final Logger logger = LoggerFactory.getLogger(QuestController.class);

    private final QuestService questService;
    private final QuestCompletionService questCompletionService;
    private final JwtTokenProvider jwtTokenProvider;

    public QuestController(QuestService questService, QuestCompletionService questCompletionService, JwtTokenProvider jwtTokenProvider) {
        this.questService = questService;
        this.questCompletionService = questCompletionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<QuestCompletionResponse> getQuestData(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Season season) {
        logger.info("시즌에 따른 퀘스트 데이터 조회 요청: {}", season);
        if (season == null) {
            season = questService.getCurrentSeason();
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        QuestCompletionResponse response = questService.getQuestDataBySeason(season, userId);

        logger.info("퀘스트 데이터 조회 결과: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getQuestImage(@PathVariable Long id) {
        QuestCompletionEntity questCompletion = questCompletionService.getQuestCompletionById(id);
        if (questCompletion != null && questCompletion.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(questCompletion.getImageData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> completeQuestPuzzle(
            @RequestHeader("Authorization") String token,
            @RequestPart("selectedSeason") String selectedSeason,
            @RequestPart("puzzleNumber") String puzzleNumber,
            @RequestPart("plantId") String plantId,
            @RequestPart("textData") String textData,
            @RequestPart("markerLatitude") String markerLatitude,
            @RequestPart("markerLongitude") String markerLongitude,
            @RequestPart("imageData") MultipartFile image
    ) {
        try {
            if (!token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰 형식입니다.");
            }
            String jwtToken = token.substring(7);
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);

            Season season = convertStringToSeason(selectedSeason);
            Long questId = convertSeasonToQuestId(season);

            byte[] imageData = image.getBytes();

            // 로깅 추가
            logger.info("완료된 퀘스트 퍼즐 요청 - userId: {}, questId: {}, puzzleNumber: {}, plantId: {}, textData: {}, latitude: {}, longitude: {}, imageDataSize: {}",
                    userId, questId, puzzleNumber, plantId, textData, markerLatitude, markerLongitude, imageData.length);

            QuestCompletionEntity result = questCompletionService.completeQuestPuzzle(
                    userId,
                    questId,
                    Integer.parseInt(puzzleNumber),
                    Long.parseLong(plantId),
                    textData,
                    imageData,
                    Double.parseDouble(markerLatitude),
                    Double.parseDouble(markerLongitude)
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("퀘스트 퍼즐 완료 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    private Season convertStringToSeason(String seasonString) {
        try {
            return Season.valueOf(seasonString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 계절 문자열입니다: " + seasonString);
        }
    }

    private Long convertSeasonToQuestId(Season season) {
        return (long) (season.ordinal() + 1);
    }
}
