package com.qnelldo.plantour.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedPuzzleInfo {
    private int puzzleNumber;
    private Long plantId;
    private String plantName;
    private LocalDateTime completedAt;

    // getters, setters, constructor
}