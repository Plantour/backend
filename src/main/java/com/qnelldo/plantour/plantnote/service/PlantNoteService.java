package com.qnelldo.plantour.plantnote.service;

import com.qnelldo.plantour.plantnote.dto.NearbyPlantNoteDTO;
import com.qnelldo.plantour.plantnote.dto.PlantNoteDTO;
import com.qnelldo.plantour.plantnote.entity.PlantNoteEntity;
import com.qnelldo.plantour.plantnote.enums.PlantInfoType;
import com.qnelldo.plantour.plantnote.repository.PlantNoteRepository;
import com.qnelldo.plantour.user.repository.UserRepository;
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

    @Value("${spring.app.base-url}")
    private String baseUrl;

    public PlantNoteService(PlantNoteRepository plantNoteRepository, UserRepository userRepository) {
        this.plantNoteRepository = plantNoteRepository;
        this.userRepository = userRepository;
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
                .map(note -> new NearbyPlantNoteDTO(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getLatitude(),
                        note.getLongitude(),
                        note.getCreatedAt(),
                        baseUrl + "/api/plant-notes/image/" + note.getId(),
                        note.getUser().getId(),
                        note.getUser().getName(),
                        note.getPlantInfoType().name(),  // Enum을 문자열로 변환
                        note.getPlantInfoType() == PlantInfoType.SELECTED ? note.getSelectedPlantId() : null,
                        note.getPlantInfoType() == PlantInfoType.CUSTOM ? note.getCustomPlantInfo() : null
                ))
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