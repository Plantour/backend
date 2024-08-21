package com.qnelldo.plantour.plant.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.plant.dto.LocalizedPlantDTO;
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
    private final LanguageContext languageContext;

    @Autowired
    public PlantService(PlantRepository plantRepository, LanguageContext languageContext) {
        this.plantRepository = plantRepository;
        this.languageContext = languageContext;
    }

    public List<LocalizedPlantDTO> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(this::convertToLocalizedDto)
                .collect(Collectors.toList());
    }

    public LocalizedPlantDTO getPlantById(Long id) {
        PlantEntity plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));
        return convertToLocalizedDto(plant);
    }


    @Transactional
    public void updatePlant(Long id, LocalizedPlantDTO updatedPlantDto) {
        PlantEntity existingPlant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));

        String currentLanguage = languageContext.getCurrentLanguage();

        // 기존 엔티티의 필드에 대한 특정 언어 데이터를 업데이트
        existingPlant.getName().put(currentLanguage, updatedPlantDto.getName());
        existingPlant.getCharacteristics1().put(currentLanguage, updatedPlantDto.getCharacteristics1());
        existingPlant.getCharacteristics2().put(currentLanguage, updatedPlantDto.getCharacteristics2());
        existingPlant.getCharacteristics3().put(currentLanguage, updatedPlantDto.getCharacteristics3());

        // 이미지 URL 및 시즌은 언어에 의존하지 않으므로 그대로 전체 업데이트
        existingPlant.setImageUrl(updatedPlantDto.getImageUrl());
        existingPlant.setSeason(updatedPlantDto.getSeason());

        // 변경된 엔티티 저장
        plantRepository.save(existingPlant);

        // 업데이트 성공 메시지를 로깅하거나 별도로 처리할 수 있습니다.
        System.out.println("식물 정보가 성공적으로 업데이트되었습니다.");
    }

    private LocalizedPlantDTO convertToLocalizedDto(PlantEntity plant) {
        LocalizedPlantDTO dto = new LocalizedPlantDTO();
        String currentLanguage = languageContext.getCurrentLanguage();

        dto.setId(plant.getId());
        dto.setName(plant.getName().get(currentLanguage));
        dto.setImageUrl(plant.getImageUrl());
        dto.setCharacteristics1(plant.getCharacteristics1().get(currentLanguage));
        dto.setCharacteristics2(plant.getCharacteristics2().get(currentLanguage));
        dto.setCharacteristics3(plant.getCharacteristics3().get(currentLanguage));
        dto.setSeason(plant.getSeason());

        return dto;
    }
}
