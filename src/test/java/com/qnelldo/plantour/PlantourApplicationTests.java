package com.qnelldo.plantour;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlantourApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualToIgnoringCase("H2");
        }
    }
}