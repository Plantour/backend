package com.qnelldo.plantour.plant.controller;

import com.qnelldo.plantour.auth.service.JwtTokenProvider;
import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.plant.dto.LocalizedPlantDTO;
import com.qnelldo.plantour.plant.dto.PlantDTO;
import com.qnelldo.plantour.plant.service.PlantService;
import com.qnelldo.plantour.user.service.UserService;
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
    private final LanguageContext languageContext;

    @Autowired
    public PlantController(PlantService plantService, LanguageContext languageContext) {
        this.plantService = plantService;
        this.languageContext = languageContext;
    }

    @GetMapping
    public ResponseEntity<List<LocalizedPlantDTO>> getAllPlants() {
        logger.info("Fetching all plants with language: {}", languageContext.getCurrentLanguage());
        return ResponseEntity.ok(plantService.getAllPlants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalizedPlantDTO> getPlantById(@PathVariable Long id) {
        logger.info("Fetching plant with id: {}", id);
        return ResponseEntity.ok(plantService.getPlantById(id));
    }
}