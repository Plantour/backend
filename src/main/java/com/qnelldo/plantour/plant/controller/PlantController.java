package com.qnelldo.plantour.plant.controller;

import com.qnelldo.plantour.plant.dto.PlantDto;
import com.qnelldo.plantour.plant.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    /**
     * 모든 식물 정보를 사용자의 언어 설정에 맞춰 조회합니다.
     * @param userId 사용자 ID
     * @return 식물 정보 리스트
     */
    @GetMapping
    public ResponseEntity<List<PlantDto>> getAllPlants(@RequestParam Long userId) {
        return ResponseEntity.ok(plantService.getAllPlants(userId));
    }

    /**
     * 특정 식물 정보를 사용자의 언어 설정에 맞춰 조회합니다.
     * @param id 식물 ID
     * @param userId 사용자 ID
     * @return 식물 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlantDto> getPlantById(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(plantService.getPlantById(id, userId));
    }
}