package com.qnelldo.plantour.plant.repository;

import com.qnelldo.plantour.plant.entity.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity, Long> {

    List<PlantEntity> findByName(String name);

    List<PlantEntity> findByScientificName(String scientificName);

    List<PlantEntity> findByBestSeason(PlantEntity.Season season);

    @Query("SELECT p FROM PlantEntity p WHERE :characteristic MEMBER OF p.characteristics")
    List<PlantEntity> findByCharacteristic(@Param("characteristic") String characteristic);

    @Query("SELECT p FROM PlantEntity p JOIN p.missions m WHERE m.id = :missionId")
    List<PlantEntity> findByMissionId(@Param("missionId") Long missionId);
}