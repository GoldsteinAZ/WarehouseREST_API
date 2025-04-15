package org.example.warehouserest_api.service;

import org.example.warehouserest_api.model.Part;
import org.example.warehouserest_api.model.PartId;
import org.example.warehouserest_api.repository.PartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Service class responsible for business logic related to part inventory management.
// Handles CRUD operations and quantity adjustments for parts in the warehouse.
@Service
public class PartService {

    // Logger for recording service operations and errors
    private static final Logger logger = LoggerFactory.getLogger(PartService.class);

    // Repository for database interactions with Part entities
    private final PartRepository partRepository;

    // Constructor-based dependency injection for PartRepository
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    // Retrieves all parts from the warehouse inventory
    @Transactional(readOnly = true)
    public List<Part> getAllParts() {
        List<Part> parts = partRepository.findAll();
        logger.info("Retrieved {} parts from inventory (Get all parts from warehouse)", parts.size());
        if (parts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parts found in the warehouse");
        }
        return parts;
    }

    // Retrieves a specific part by its composite ID (material, serial, supplier numbers)
    @Transactional(readOnly = true)
    public Part getPartById(String materialNumber, String serialNumber, String supplierNumber) {
        PartId partId = new PartId(materialNumber, serialNumber, supplierNumber);
        // Find part or throw exception if not found
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> {
                    logger.warn("Part with ID {} not found", partId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Part with ID '%s' not found", partId));
                });
        logger.info("Retrieved part {}", partId);
        return part;
    }

    // Adds a new part to the warehouse with initial quantity of zero
    @Transactional
    public Part addPart(String materialNumber, String serialNumber, String supplierNumber) {
        PartId partId = new PartId(materialNumber, serialNumber, supplierNumber);
        // Check if part already exists to avoid duplicate entries
        if (partRepository.existsById(partId)) {
            logger.warn("Cannot add part {} because it already exists", partId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Cannot add part '%s' because it already exists", partId));
        }
        // Create and save new part with initial quantity of 0
        Part newPart = new Part(partId, 0);
        Part saved = partRepository.save(newPart);
        logger.info("Added new part {} with initial quantity {}", partId, saved.getQuantity());
        return saved;
    }

    // Deletes a part from the warehouse, but only if its quantity is zero
    @Transactional
    public void deletePart(String materialNumber, String serialNumber, String supplierNumber) {
        PartId partId = new PartId(materialNumber, serialNumber, supplierNumber);
        // Find part or throw exception if not found
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Part with ID '%s' not found", partId)));
        // Check if quantity is zero before allowing deletion
        if (part.getQuantity() != 0) {
            logger.warn("Cannot delete part {} because quantity is {} (not zero)", partId, part.getQuantity());
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Cannot delete part '%s' because quantity is '%s' (not zero)", partId, part.getQuantity()));
        }
        // Deletes a part
        partRepository.delete(part);
        logger.info("Deleted part {} from inventory", partId);
    }

    // Increases the quantity of a part by the specified amount
    @Transactional
    public Part increaseQuantity(String materialNumber, String serialNumber, String supplierNumber, int amount) {
        PartId partId = new PartId(materialNumber, serialNumber, supplierNumber);
        // Find part or throw exception if not found
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Part with ID '%s' not found (increase operation)", partId)
                ));
        // Validate that amount is positive
        if (amount < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Amount must be greater than 0 for part '%s' (during increase operation)", partId)
            );
        }
        // Increase quantity
        part.setQuantity(part.getQuantity() + amount);
        Part saved = partRepository.save(part);
        logger.info("Increased quantity for part {} by {} (new quantity: {})", partId, amount, saved.getQuantity());
        return saved;
    }

    // Decreases the quantity of a part by the specified amount, but never below zero
    @Transactional
    public Part decreaseQuantity(String materialNumber, String serialNumber, String supplierNumber, int amount) {
        PartId partId = new PartId(materialNumber, serialNumber, supplierNumber);
        // Find part or throw exception if not found
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Part with ID '%s' not found (decrease operation)", partId)
                ));
        // Validate that amount is positive
        if (amount < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Amount must be greater than 0 for part '%s' (during decrease operation)", partId)
            );
        }
        // Check if there's enough quantity to subtract
        if (amount > part.getQuantity()) {
            logger.warn("Cannot subtract {} from part {} because current quantity is only {}", amount, partId, part.getQuantity());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("Cannot subtract '%s' from part '%s' because current quantity is only '%s'", amount, partId, part.getQuantity())
            );
        }
        // Decrease quantity, save and log operation
        part.setQuantity(part.getQuantity() - amount);
        Part saved = partRepository.save(part);
        logger.info("Decreased quantity for part {} by {} (new quantity: {})", partId, amount, saved.getQuantity());
        return saved;
    }
}
