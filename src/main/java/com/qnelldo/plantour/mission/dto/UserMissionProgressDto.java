package com.qnelldo.plantour.mission.dto;


import com.qnelldo.plantour.enums.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionProgressDto {
    private Long userId;
    private String username;
    private Season currentSeason;
    private List<CompletedPuzzleInfo> completedPuzzles;

    // getters, setters, constructor
}