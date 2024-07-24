package com.qnelldo.plantour.plant.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Data
@Entity
@Table(name = "plants")
public class PlantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String nameJson;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String characteristics1Json;

    @Column(columnDefinition = "TEXT")
    private String characteristics2Json;

    @Column(columnDefinition = "TEXT")
    private String characteristics3Json;

    private String season;

    // Getter and Setter for JSON fields
    public Map<String, String> getName() {
        return convertJsonToMap(nameJson);
    }

    public void setName(Map<String, String> name) {
        this.nameJson = convertMapToJson(name);
    }

    public Map<String, String> getCharacteristics1() {
        return convertJsonToMap(characteristics1Json);
    }

    public void setCharacteristics1(Map<String, String> characteristics) {
        this.characteristics1Json = convertMapToJson(characteristics);
    }

    public Map<String, String> getCharacteristics2() {
        return convertJsonToMap(characteristics2Json);
    }

    public void setCharacteristics2(Map<String, String> characteristics) {
        this.characteristics2Json = convertMapToJson(characteristics);
    }

    public Map<String, String> getCharacteristics3() {
        return convertJsonToMap(characteristics3Json);
    }

    public void setCharacteristics3(Map<String, String> characteristics) {
        this.characteristics3Json = convertMapToJson(characteristics);
    }

    private Map<String, String> convertJsonToMap(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    private String convertMapToJson(Map<String, String> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            return null;
        }
    }
}