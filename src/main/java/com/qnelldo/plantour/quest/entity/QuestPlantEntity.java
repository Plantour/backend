package com.qnelldo.plantour.quest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quest_plants")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestPlantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private QuestEntity quest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private PlantEntity plant;
}
