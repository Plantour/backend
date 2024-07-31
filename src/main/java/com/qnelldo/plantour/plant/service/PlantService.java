package com.qnelldo.plantour.plant.service;

import com.qnelldo.plantour.plant.dto.PlantDto;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlantService(PlantRepository plantRepository, UserRepository userRepository) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
    }

    /**
     * 모든 식물 정보를 사용자의 언어 설정에 맞춰 조회합니다.
     * @param userId 사용자 ID
     * @return 식물 정보 리스트
     */
    public List<PlantDto> getAllPlants(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String languageCode = user.getLanguageCode();
        return plantRepository.findAll().stream()
                .map(plant -> convertToDto(plant, languageCode))
                .collect(Collectors.toList());
    }

    /**
     * 특정 식물 정보를 사용자의 언어 설정에 맞춰 조회합니다.
     * @param id 식물 ID
     * @param userId 사용자 ID
     * @return 식물 정보
     */
    public PlantDto getPlantById(Long id, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String languageCode = user.getLanguageCode();
        PlantEntity plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));
        return convertToDto(plant, languageCode);
    }

    private PlantDto convertToDto(PlantEntity plant, String languageCode) {
        PlantDto dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName().getOrDefault(languageCode, plant.getName().get("ENG")));
        dto.setImage(plant.getImageUrl());
        dto.setCharacteristics1(plant.getCharacteristics1().getOrDefault(languageCode, plant.getCharacteristics1().get("ENG")));
        dto.setCharacteristics2(plant.getCharacteristics2().getOrDefault(languageCode, plant.getCharacteristics2().get("ENG")));
        dto.setCharacteristics3(plant.getCharacteristics3().getOrDefault(languageCode, plant.getCharacteristics3().get("ENG")));
        return dto;
    }
}