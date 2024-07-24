package com.qnelldo.plantour.mission.entity;

import com.qnelldo.plantour.plant.entity.PlantEntity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "mission_plants")
public class MissionPlantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionEntity mission;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private PlantEntity plant;

    // Getters and setters
}