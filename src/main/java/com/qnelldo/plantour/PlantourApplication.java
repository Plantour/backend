package com.qnelldo.plantour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class PlantourApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlantourApplication.class, args);
    }
}