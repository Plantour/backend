package com.qnelldo.plantour.plantnote.dto;

import com.qnelldo.plantour.plantnote.enums.PlantInfoType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PlantNoteDTO {
    private String title;
    private String content;
    private MultipartFile image;
    private PlantInfoType plantInfoType;
    private Long selectedPlantId;
    private String customPlantInfo;
    private Double latitude;
    private Double longitude;
}