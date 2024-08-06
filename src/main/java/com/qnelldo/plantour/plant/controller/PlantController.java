package com.qnelldo.plantour.plant.controller;

import com.qnelldo.plantour.plant.dto.PlantDto;
import com.qnelldo.plantour.plant.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private static final Logger logger = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;

    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

//    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PlantDto>> getAllPlants() {
        logger.info("Fetching all plants for user: {}");
        return ResponseEntity.ok(plantService.getAllPlants());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PlantDto> getPlantById(@PathVariable Long id, @RequestParam Long userId) {
        logger.info("Fetching plant with id: {} for user: {}", id, userId);
        return ResponseEntity.ok(plantService.getPlantById(id, userId));
    }
}