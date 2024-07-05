package com.qnelldo.plantour.mission.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionDto {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;

    public MissionDto(Long id, String name, String description, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}