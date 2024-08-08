package com.qnelldo.plantour.plantnote.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.plantnote.dto.NearbyPlantNoteDTO;
import com.qnelldo.plantour.plantnote.dto.PlantNoteDTO;
import com.qnelldo.plantour.plantnote.entity.PlantNoteEntity;
import com.qnelldo.plantour.plantnote.enums.PlantInfoType;
import com.qnelldo.plantour.plantnote.service.PlantNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plant-notes")
public class PlantNoteController {
    private static final Logger logger = LoggerFactory.getLogger(PlantNoteController.class);

    private final PlantNoteService plantNoteService;
    private final JwtTokenProvider jwtTokenProvider;

    public PlantNoteController(PlantNoteService plantNoteService, JwtTokenProvider jwtTokenProvider) {
        this.plantNoteService = plantNoteService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPlantNote(
            @RequestHeader("Authorization") String token,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "infoType") String plantInfoType,
            @RequestPart(value = "plantId", required = false) String selectedPlantId,
            @RequestPart(value = "plantInfo", required = false) String customPlantInfo,
            @RequestPart("latitude") String latitude,
            @RequestPart("longitude") String longitude,
            @RequestPart("image") MultipartFile image
    ) {
        try {
            if (!token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰 형식입니다.");
            }
            String jwtToken = token.substring(7);
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);

            PlantNoteDTO plantNoteDTO = new PlantNoteDTO();
            plantNoteDTO.setTitle(title);
            plantNoteDTO.setContent(content);
            plantNoteDTO.setPlantInfoType(PlantInfoType.valueOf(plantInfoType.toUpperCase()));
            plantNoteDTO.setSelectedPlantId(selectedPlantId != null ? Long.parseLong(selectedPlantId) : null);
            plantNoteDTO.setCustomPlantInfo(customPlantInfo);
            plantNoteDTO.setLatitude(Double.parseDouble(latitude));
            plantNoteDTO.setLongitude(Double.parseDouble(longitude));
            plantNoteDTO.setImage(image);

            logger.info("식물 노트 생성 요청 - userId: {}, title: {}, plantInfoType: {}, latitude: {}, longitude: {}",
                    userId, title, plantInfoType, latitude, longitude);

            PlantNoteEntity createdNote = plantNoteService.createPlantNote(plantNoteDTO, userId);
            return ResponseEntity.ok(createdNote);
        } catch (Exception e) {
            logger.error("식물 노트 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<Map<String, List<NearbyPlantNoteDTO>>> getNearbyPlantNotes(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        double radiusKm = 1.0;
        Map<String, List<NearbyPlantNoteDTO>> nearbyNotes = plantNoteService.getNearbyPlantNotes(latitude, longitude, radiusKm);
        return ResponseEntity.ok(nearbyNotes);
    }

    @PutMapping("/{noteId}/plant-info")
    public ResponseEntity<?> updatePlantInfo(
            @PathVariable Long noteId,
            @RequestParam PlantInfoType newType,
            @RequestParam(required = false) Long selectedPlantId,
            @RequestParam(required = false) String customInfo) {
        try {
            PlantNoteEntity updatedNote = plantNoteService.updatePlantInfo(noteId, newType, selectedPlantId, customInfo);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            logger.error("식물 정보 업데이트 중 오류 발생", e);
            return ResponseEntity.badRequest().body("식물 정보 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    // 이미지를 가져오는 엔드포인트 추가
    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPlantNoteImage(@PathVariable Long id) {
        PlantNoteEntity plantNote = plantNoteService.getPlantNoteById(id);
        if (plantNote != null && plantNote.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(plantNote.getImageData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}