package com.qnelldo.plantour.plant.controller;

import com.qnelldo.plantour.plant.entity.PlantEntity;
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

    @PostMapping
    public ResponseEntity<PlantEntity> createPlant(@RequestBody PlantEntity plant) {
        PlantEntity savedPlant = plantService.savePlant(plant);
        return ResponseEntity.ok(savedPlant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantEntity> getPlant(@PathVariable Long id) {
        return plantService.getPlantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PlantEntity>> getAllPlants() {
        List<PlantEntity> plants = plantService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantEntity> updatePlant(@PathVariable Long id, @RequestBody PlantEntity plant) {
        return plantService.getPlantById(id)
                .map(existingPlant -> {
                    plant.setId(id);
                    return ResponseEntity.ok(plantService.savePlant(plant));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        return plantService.getPlantById(id)
                .map(plant -> {
                    plantService.deletePlant(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlantEntity>> searchPlants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String scientificName,
            @RequestParam(required = false) PlantEntity.Season season,
            @RequestParam(required = false) String characteristic) {

        if (name != null) {
            return ResponseEntity.ok(plantService.getPlantsByName(name));
        } else if (scientificName != null) {
            return ResponseEntity.ok(plantService.getPlantsByScientificName(scientificName));
        } else if (season != null) {
            return ResponseEntity.ok(plantService.getPlantsByBestSeason(season));
        } else if (characteristic != null) {
            return ResponseEntity.ok(plantService.getPlantsByCharacteristic(characteristic));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<PlantEntity>> getPlantsByMission(@PathVariable Long missionId) {
        List<PlantEntity> plants = plantService.getPlantsByMissionId(missionId);
        return ResponseEntity.ok(plants);
    }
}