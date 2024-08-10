package com.qnelldo.plantour.plant.controller;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.plant.dto.PlantDTO;
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
    private final LanguageContext languageContext;

    @Autowired
    public PlantController(PlantService plantService, LanguageContext languageContext) {
        this.languageContext = languageContext;
        this.plantService = plantService;
    }

    @GetMapping
    public ResponseEntity<List<PlantDTO>> getAllPlants(@RequestParam(value = "lang", required = false) String lang,
                                                       @RequestParam(value = "userId", required = false) Long userId) {
        if (userId != null) {
            plantService.setLanguageFromUser(userId);
        } else if (lang != null) {
            languageContext.setCurrentLanguage(lang);
        }
        logger.info("Fetching all plants");
        return ResponseEntity.ok(plantService.getAllPlants());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PlantDTO> getPlantById(@PathVariable Long id, @RequestParam Long userId) {
        plantService.setLanguageFromUser(userId);
        logger.info("Fetching plant with id: {} for user: {}", id, userId);
        return ResponseEntity.ok(plantService.getPlantById(id));
    }
}