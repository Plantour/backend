package com.qnelldo.plantour.quest.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PlantDTO {
    private Long id;
    private Map<String, String> name;  // 다국어 지원을 위해 Map으로 변경
    private String imageUrl;
    private Map<String, String> characteristics1;
    private Map<String, String> characteristics2;
    private Map<String, String> characteristics3;
    private String season;
}