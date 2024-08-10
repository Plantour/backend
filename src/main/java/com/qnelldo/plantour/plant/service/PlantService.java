package com.qnelldo.plantour.plant.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.plant.dto.PlantDTO;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final LanguageContext languageContext;

    @Autowired
    public PlantService(PlantRepository plantRepository, UserRepository userRepository, LanguageContext languageContext) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.languageContext = languageContext;
    }

    public List<PlantDTO> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PlantDTO getPlantById(Long id) {
        PlantEntity plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));
        return convertToDto(plant);
    }

    public void setLanguageFromUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        languageContext.setCurrentLanguage(user.getLanguageCode());
    }

    @Transactional
    public PlantDTO updatePlant(Long id, PlantDTO updatedPlantDto) {
        PlantEntity existingPlant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));

        // 기존 엔티티 데이터를 DTO의 데이터로 업데이트
        existingPlant.setName(updatedPlantDto.getName());
        existingPlant.setImageUrl(updatedPlantDto.getImageUrl());
        existingPlant.setCharacteristics1(updatedPlantDto.getCharacteristics1());
        existingPlant.setCharacteristics2(updatedPlantDto.getCharacteristics2());
        existingPlant.setCharacteristics3(updatedPlantDto.getCharacteristics3());
        existingPlant.setSeason(updatedPlantDto.getSeason());

        // 변경된 엔티티 저장
        plantRepository.save(existingPlant);

        return convertToDto(existingPlant);
    }

    private PlantDTO convertToDto(PlantEntity plant) {
        PlantDTO dto = new PlantDTO();

        dto.setId(plant.getId());
        dto.setName(plant.getName());  // Map 그대로 할당
        dto.setImageUrl(plant.getImageUrl());
        dto.setCharacteristics1(plant.getCharacteristics1());  // Map 그대로 할당
        dto.setCharacteristics2(plant.getCharacteristics2());  // Map 그대로 할당
        dto.setCharacteristics3(plant.getCharacteristics3());  // Map 그대로 할당
        dto.setSeason(plant.getSeason());

        return dto;
    }
}
