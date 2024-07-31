package com.qnelldo.plantour.quest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qnelldo.plantour.common.enums.Season;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "quests")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Season season;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestPlantEntity> questPlants = new ArrayList<>();

    @Column(name = "completed_puzzles")
    private int completedPuzzles = 0;

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
