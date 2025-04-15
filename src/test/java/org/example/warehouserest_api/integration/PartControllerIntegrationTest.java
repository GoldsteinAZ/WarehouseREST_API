package org.example.warehouserest_api.integration;

import org.example.warehouserest_api.model.Part;
import org.example.warehouserest_api.model.PartId;
import org.example.warehouserest_api.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

// Integration tests for the PartController.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartControllerIntegrationTest {

    // TestRestTemplate is autowired with the random port value
    @Autowired
    private TestRestTemplate restTemplate;

    // Real repository is used to prepare test data and verify results
    @Autowired
    private PartRepository partRepository;

    //Setup method runs before each test.
    // Clears all data from the repository to ensure a clean test environment.
    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    // Tests adding a new part through the API.
    @Test
    void testAddPart_Success() {
        // Arrange
        PartId partId = new PartId("M1", "S1", "SUP1");

        // Act
        ResponseEntity<Part> response = restTemplate.postForEntity("/api/v1/parts", partId, Part.class);

        // Assert
        assertEquals(CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(partId, response.getBody().getId());
        assertEquals(0, response.getBody().getQuantity());
    }

    // Tests retrieving a non-existent part.
    @Test
    void testGetPartById_NotFound() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/parts/M2/S2/SUP2", String.class);

        // Assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    // Tests retrieving all parts when the repository is empty.
    @Test
    void testGetAllParts_EmptyRepository() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/parts", String.class);

        // Assert
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("No parts found in the warehouse"));
    }

    // Tests increasing the quantity of a part.
    @Test
    void testIncreaseQuantity() {
        // Arrange
        PartId partId = new PartId("M3", "S3", "SUP3");
        restTemplate.postForEntity("/api/v1/parts", partId, Part.class);

        // Act
        String url = "/api/v1/parts/M3/S3/SUP3/add?amount=5";
        ResponseEntity<Part> response = restTemplate.exchange(
                url, HttpMethod.PATCH, HttpEntity.EMPTY, Part.class);

        // Assert
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getQuantity());
    }

    // Tests decreasing the quantity of a part.
    @Test
    void testDecreaseQuantity() {
        // Arrange
        PartId partId = new PartId("M4", "S4", "SUP4");
        restTemplate.postForEntity("/api/v1/parts", partId, Part.class);
        restTemplate.exchange(
                "/api/v1/parts/M4/S4/SUP4/add?amount=10",
                HttpMethod.PATCH, HttpEntity.EMPTY, Part.class);

        // Act
        String url = "/api/v1/parts/M4/S4/SUP4/subtract?amount=3";
        ResponseEntity<Part> response = restTemplate.exchange(
                url, HttpMethod.PATCH, HttpEntity.EMPTY, Part.class);

        // Assert
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7, response.getBody().getQuantity());
    }

    // Tests deleting a part.
    @Test
    void testDeletePart() {
        /// Arrange - First add a part (with quantity 0)
        PartId partId = new PartId("M5", "S5", "SUP5");
        restTemplate.postForEntity("/api/v1/parts", partId, Part.class);

        // Act
        String url = "/api/v1/parts/M5/S5/SUP5";
        ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertEquals(NO_CONTENT, response.getStatusCode());

        // Assert
        ResponseEntity<String> getResponse = restTemplate.getForEntity(url, String.class);
        assertEquals(NOT_FOUND, getResponse.getStatusCode());
    }
}
