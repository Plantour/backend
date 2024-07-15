package com.qnelldo.plantour.mission.entity;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "missions")
public class MissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Season season;

    /**
     * 미션은 9개의 퍼즐 조각으로 구성됩니다.
     * 각 퍼즐 조각은 1부터 9까지의 번호를 가집니다.
     * 완성된 퍼즐 조각의 수를 추적하기 위해 completedPuzzles 필드를 사용합니다.
     */
    @Column(name = "completed_puzzles")
    private int completedPuzzles = 0;

    @ManyToMany
    @JoinTable(
            name = "mission_plants",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "plant_id")
    )
    private List<PlantEntity> plants;

    /**
     * 퍼즐 조각 완성 시 호출되는 메서드
     * @return 모든 퍼즐이 완성되었는지 여부
     */
    public boolean completePuzzle() {
        if (completedPuzzles < 9) {
            completedPuzzles++;
        }
        return completedPuzzles == 9;
    }
}