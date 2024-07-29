package com.qnelldo.plantour.mission.entity;

import com.qnelldo.plantour.common.enums.Season;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missions")
@Data
public class MissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Season season;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionPlantEntity> missionPlants = new ArrayList<>();

    @Column(name = "completed_puzzles")
    private int completedPuzzles = 0;

//    public void addPlant(PlantEntity plant, int puzzleNumber) {
//        MissionPlantEntity missionPlant = new MissionPlantEntity(this, plant, puzzleNumber);
//        missionPlants.add(missionPlant);
//    }

    public boolean completePuzzle() {
        if (completedPuzzles < 9) {
            completedPuzzles++;
        }
        return completedPuzzles == 9;
    }

    public int getCompletedPuzzlesCount() {
        return completedPuzzles;
    }
}