package com.qnelldo.plantour.plant.entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "plants")
public class PlantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private String image; // BASE64 encoded image

    private String characteristics1;
    private String characteristics2;
    private String characteristics3;

}
