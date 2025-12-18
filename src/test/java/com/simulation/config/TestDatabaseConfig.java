package com.simulation.config;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class TestDatabaseConfig {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withCommand(
                        "postgres",
                        "-c", "log_statement=all",
                        "-c", "log_duration=on",
                        "-c", "log_line_prefix=%m [%p] %u@%d "
                );
        postgresContainer.start();

        // PostgreSQL JDBC URL (불필요한 옵션 제거)
        String jdbcUrl = postgresContainer.getJdbcUrl()
                + "?rewriteBatchedInserts=true";

        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());

    }

    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return postgresContainer;
    }

    @PreDestroy
    public void stop() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            postgresContainer.stop();
        }
    }
}