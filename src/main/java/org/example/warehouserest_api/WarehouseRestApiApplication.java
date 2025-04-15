package org.example.warehouserest_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;

// Main Spring Boot application class for the Warehouse REST API.
@SpringBootApplication
public class WarehouseRestApiApplication {

    // Logger for recording application lifecycle events
    private static final Logger logger = LoggerFactory.getLogger(WarehouseRestApiApplication.class);

    // Main method to start the Spring Boot application.
    public static void main(String[] args) {
        SpringApplication.run(WarehouseRestApiApplication.class, args);
    }

    // Defines a listener for the application ready event.
    // This is triggered when the application is fully started and ready to handle requests.
    @Bean
    public ApplicationListener<ApplicationReadyEvent> onApplicationReady() {
        return event -> {
            logger.info("Warehouse REST API successfully started at {}", LocalDateTime.now());
        };
    }

    // Handles the application shutdown event.
    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        logger.info("Warehouse REST API shutting down at {}", LocalDateTime.now());
    }
}
