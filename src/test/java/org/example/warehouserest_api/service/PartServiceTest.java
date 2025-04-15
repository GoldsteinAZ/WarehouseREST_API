package org.example.warehouserest_api.service;

import org.example.warehouserest_api.model.Part;
import org.example.warehouserest_api.model.PartId;
import org.example.warehouserest_api.repository.PartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Unit tests for the PartService class
@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    // Mock repository to simulate database interactions without actual DB
    @Mock
    private PartRepository partRepository;

    // Inject mocks into the service being tested
    @InjectMocks
    private PartService partService;

    // Tests the successful creation of a new part.
    @Test
    void testAddPart_Success() {
        // Arrange
        PartId id = new PartId("M1", "S1", "SUP1");
        when(partRepository.existsById(id)).thenReturn(false);
        Part savedPart = new Part(id, 0);
        when(partRepository.save(any(Part.class))).thenReturn(savedPart);

        // Act
        Part result = partService.addPart("M1", "S1", "SUP1");

        // Verify repository interactions
        verify(partRepository).existsById(id);
        ArgumentCaptor<Part> partCaptor = ArgumentCaptor.forClass(Part.class);
        verify(partRepository).save(partCaptor.capture());
        Part toSave = partCaptor.getValue();
        assertEquals(0, toSave.getQuantity());
        assertEquals(id, toSave.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        assertEquals("M1", result.getId().getMaterialNumber());
    }

    // Tests adding a part that already exists.
    @Test
    void testAddPart_AlreadyExists() {
        // Arrange
        PartId id = new PartId("M1", "S1", "SUP1");
        when(partRepository.existsById(id)).thenReturn(true);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partService.addPart("M1", "S1", "SUP1")
        );

        // Assert
        assertEquals(409, exception.getStatusCode().value());
        verify(partRepository, never()).save(any(Part.class));
    }

    // Tests successful quantity increase for a part.
    @Test
    void testIncreaseQuantity_Success() {
        // Arrange
        PartId id = new PartId("M2", "S2", "SUP2");
        Part existing = new Part(id, 5);
        when(partRepository.findById(id)).thenReturn(Optional.of(existing));
        // Simulate save by returning the provided argument
        when(partRepository.save(any(Part.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Part result = partService.increaseQuantity("M2", "S2", "SUP2", 3);

        // Assert
        verify(partRepository).findById(id);
        verify(partRepository).save(any(Part.class));
        assertEquals(8, result.getQuantity());
    }

    // Tests successful quantity decrease for a part.
    @Test
    void testDecreaseQuantity_Success() {
        // Arrange
        PartId id = new PartId("M3", "S3", "SUP3");
        Part existing = new Part(id, 10);
        when(partRepository.findById(id)).thenReturn(Optional.of(existing));
        when(partRepository.save(any(Part.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Part result = partService.decreaseQuantity("M3", "S3", "SUP3", 4);

        //Assert
        verify(partRepository).findById(id);
        verify(partRepository).save(any(Part.class));
        assertEquals(6, result.getQuantity());
    }

    // Tests decreasing quantity by more than available.
    @Test
    void testDecreaseQuantity_TooMuch() {
        // Arrange
        PartId id = new PartId("M3", "S3", "SUP3");
        Part existing = new Part(id, 2);

        when(partRepository.findById(id)).thenReturn(Optional.of(existing));
        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partService.decreaseQuantity("M3", "S3", "SUP3", 5)
        );

        // Assert
        assertEquals(409, exception.getStatusCode().value());

        // Verify save wasn't called (operation was aborted)
        verify(partRepository, never()).save(any(Part.class));
    }

    // Tests successful deletion of a part with zero quantity.
    @Test
    void testDeletePart_Success() {
        // Arrange
        PartId id = new PartId("X1", "Y1", "Z1");
        Part existing = new Part(id, 0);
        when(partRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(partRepository).delete(existing);

        // Act
        assertDoesNotThrow(() -> partService.deletePart("X1", "Y1", "Z1"));

        // Assert
        verify(partRepository).delete(existing);
    }

    // Tests deleting a part with non-zero quantity.
    @Test
    void testDeletePart_NonZeroQuantity() {
        // Arrange
        PartId id = new PartId("X2", "Y2", "Z2");
        Part existing = new Part(id, 5);
        when(partRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partService.deletePart("X2", "Y2", "Z2")
        );

        // Assert
        assertEquals(409, exception.getStatusCode().value());
        verify(partRepository, never()).delete(any(Part.class));
    }
}
