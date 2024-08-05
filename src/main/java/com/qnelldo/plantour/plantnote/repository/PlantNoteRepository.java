package com.qnelldo.plantour.plantnote.repository;

import com.qnelldo.plantour.plantnote.entity.PlantNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantNoteRepository extends JpaRepository<PlantNoteEntity, Long> {
    @Query(value = "SELECT * FROM plant_notes " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(latitude)))) <= :radius",
            nativeQuery = true)
    List<PlantNoteEntity> findNearbyPlantNote(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radiusKm
    );
}