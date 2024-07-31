package com.qnelldo.plantour.quest.dto;


import com.qnelldo.plantour.common.enums.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestProgressDto {
    private Long userId;
    private String username;
    private Season currentSeason;
    private List<CompletedPuzzleInfo> completedPuzzles;

    // getters, setters, constructor
}