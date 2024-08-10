package com.qnelldo.plantour.plantnote.service;

import com.qnelldo.plantour.common.context.LanguageContext;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.plantnote.dto.NearbyPlantNoteDTO;
import com.qnelldo.plantour.plantnote.dto.PlantNoteDTO;
import com.qnelldo.plantour.plantnote.entity.PlantNoteEntity;
import com.qnelldo.plantour.plantnote.enums.PlantInfoType;
import com.qnelldo.plantour.plantnote.repository.PlantNoteRepository;
import com.qnelldo.plantour.user.repository.UserRepository;
import com.qnelldo.plantour.user.service.NicknameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlantNoteService {

    private static final Logger logger = LoggerFactory.getLogger(PlantNoteService.class);

    private final PlantNoteRepository plantNoteRepository;
    private final UserRepository userRepository;
    private final NicknameService nicknameService;
    private final PlantRepository plantRepository;
    private final LanguageContext languageContext;

    @Value("${spring.app.base-url}")
    private String baseUrl;

    public PlantNoteService(LanguageContext languageContext, PlantRepository plantRepository, PlantNoteRepository plantNoteRepository, UserRepository userRepository, NicknameService nicknameService) {
        this.languageContext = languageContext;
        this.plantRepository = plantRepository;
        this.plantNoteRepository = plantNoteRepository;
        this.userRepository = userRepository;
        this.nicknameService = nicknameService;

    }

    @Transactional
    public PlantNoteEntity createPlantNote(PlantNoteDTO dto, Long userId) throws Exception {
        logger.info("Creating plant note for user: {}", userId);

        PlantNoteEntity entity = new PlantNoteEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setImageData(dto.getImage().getBytes());
        entity.setPlantInfoType(dto.getPlantInfoType());
        entity.setSelectedPlantId(dto.getSelectedPlantId());
        entity.setCustomPlantInfo(dto.getCustomPlantInfo());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setCreatedAt(LocalDateTime.now());

        entity.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId)));

        PlantNoteEntity savedEntity = plantNoteRepository.save(entity);
        logger.info("Plant note created with ID: {}", savedEntity.getId());
        return savedEntity;
    }

    public Map<String, List<NearbyPlantNoteDTO>> getNearbyPlantNotes(double latitude, double longitude, double radiusKm) {
        logger.info("Fetching nearby plant notes at ({}, {}) within {}km", latitude, longitude, radiusKm);

        List<PlantNoteEntity> nearbyNotes = plantNoteRepository.findNearbyPlantNote(latitude, longitude, radiusKm);

        List<NearbyPlantNoteDTO> noteDTOs = nearbyNotes.stream()
                .map(note -> {
                    String localizedNickname = nicknameService.getLocalizedNickname(note.getUser().getId());
                    // SELECTED 타입일 경우, plantId를 사용해 다국어 이름 가져오기

                    String plantName = null;
                    if (note.getPlantInfoType() == PlantInfoType.SELECTED && note.getSelectedPlantId() != null) {
                        PlantEntity plant = plantRepository.findById(note.getSelectedPlantId())
                                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));
                        plantName = plant.getName().getOrDefault(languageContext.getCurrentLanguage(), plant.getName().get("ENG"));
                    }

                    return new NearbyPlantNoteDTO(
                            note.getId(),
                            note.getTitle(),
                            note.getContent(),
                            note.getLatitude(),
                            note.getLongitude(),
                            note.getCreatedAt(),
                            baseUrl + "/api/plant-notes/image/" + note.getId(),
                            note.getUser().getId(),
                            note.getUser().getName(),
                            localizedNickname,
                            note.getPlantInfoType().name(),
                            plantName,
                            note.getPlantInfoType() == PlantInfoType.CUSTOM ? note.getCustomPlantInfo() : null
                    );
                })
                .collect(Collectors.toList());

        logger.info("Found {} nearby plant notes", noteDTOs.size());
        return Map.of("nearbyPlantNotes", noteDTOs);
    }


    @Transactional
    public PlantNoteEntity updatePlantInfo(Long noteId, PlantInfoType newType, Long selectedPlantId, String customInfo) {
        logger.info("Updating plant info for note: {}", noteId);

        PlantNoteEntity note = plantNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note ID: " + noteId));

        note.setPlantInfoType(newType);
        note.setSelectedPlantId(selectedPlantId);
        note.setCustomPlantInfo(customInfo);

        PlantNoteEntity updatedNote = plantNoteRepository.save(note);
        logger.info("Plant info updated for note: {}", noteId);
        return updatedNote;
    }

    public PlantNoteEntity getPlantNoteById(Long noteId) {
        logger.info("Fetching plant note with ID: {}", noteId);
        return plantNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note ID: " + noteId));
    }
}