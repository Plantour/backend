package com.qnelldo.plantour.plant.service;

import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {

    private final PlantRepository plantRepository;

    @Autowired
    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public PlantEntity savePlant(PlantEntity plant) {
        return plantRepository.save(plant);
    }

    public Optional<PlantEntity> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    public List<PlantEntity> getAllPlants() {
        return plantRepository.findAll();
    }

    public void deletePlant(Long id) {
        plantRepository.deleteById(id);
    }

    public List<PlantEntity> getPlantsByName(String name) {
        return plantRepository.findByName(name);
    }

    public List<PlantEntity> getPlantsByScientificName(String scientificName) {
        return plantRepository.findByScientificName(scientificName);
    }

    public List<PlantEntity> getPlantsByBestSeason(PlantEntity.Season season) {
        return plantRepository.findByBestSeason(season);
    }

    public List<PlantEntity> getPlantsByCharacteristic(String characteristic) {
        return plantRepository.findByCharacteristic(characteristic);
    }

    public List<PlantEntity> getPlantsByMissionId(Long missionId) {
        return plantRepository.findByMissionId(missionId);
    }
}