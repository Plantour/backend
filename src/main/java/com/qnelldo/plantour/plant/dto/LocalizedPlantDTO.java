package com.qnelldo.plantour.plant.dto;

import lombok.Data;

@Data
public class LocalizedPlantDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String characteristics1;
    private String characteristics2;
    private String characteristics3;
    private String season;
}