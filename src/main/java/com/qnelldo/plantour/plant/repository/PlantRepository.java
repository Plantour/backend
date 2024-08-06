package com.qnelldo.plantour.plant.repository;

import com.qnelldo.plantour.plant.entity.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity, Long> {

    @Override
    Optional<PlantEntity> findById(Long id);
}