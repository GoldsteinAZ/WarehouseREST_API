package org.example.warehouserest_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.warehouserest_api.model.Part;
import org.example.warehouserest_api.model.PartId;
import org.example.warehouserest_api.service.PartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST Controller that exposes API endpoints for part inventory management.
// Handles HTTP requests for CRUD operations on parts.
@RestController
@RequestMapping("/api/v1/parts")
@Tag(name = "Parts", description = "API for managing parts in the warehouse")
public class PartController {

    // Service to delegate business logic operations
    private final PartService partService;

    // Constructor-based dependency injection for PartService
    public PartController(PartService partService) {
        this.partService = partService;
    }

    // GET endpoint to retrieve all parts from the warehouse
    // GET /api/v1/parts
    @Operation(summary = "Get all parts", description = "Retrieve a list of all parts in the warehouse")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of parts returned successfully"),
            @ApiResponse(responseCode = "404", description = "No parts found in the warehouse")
    })
    @GetMapping
    public List<Part> getAllParts() {
        return partService.getAllParts();
    }

    // GET endpoint to retrieve a specific part by its composite ID
    // GET /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}
    @Operation(summary = "Get part by ID", description = "Retrieve a specific part by its composite identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Part found and returned"),
            @ApiResponse(responseCode = "404", description = "Part not found")
    })
    @GetMapping("/{materialNumber}/{serialNumber}/{supplierNumber}")
    public Part getPartById(
            @Parameter(description = "Material number", example = "MAT100") @PathVariable String materialNumber,
            @Parameter(description = "Serial number", example = "SER100") @PathVariable String serialNumber,
            @Parameter(description = "Supplier number", example = "SUP100") @PathVariable String supplierNumber) {
        return partService.getPartById(materialNumber, serialNumber, supplierNumber);
    }

    // POST endpoint to add a new part with initial quantity of zero
    // POST /api/v1/parts (identifier in body JSON)
    @Operation(summary = "Add new part", description = "Add a new part to the warehouse (initial quantity is 0)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Part created successfully"),
            @ApiResponse(responseCode = "409", description = "Part already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<Part> addPart(
            @Parameter(description = "Part identifier (materialNumber, serialNumber, supplierNumber)", required = true)
            @Valid @RequestBody PartId partId) {
        Part created = partService.addPart(partId.getMaterialNumber(), partId.getSerialNumber(), partId.getSupplierNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE endpoint to remove a part from the warehouse (only if quantity is zero)
    // DELETE /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber} - usuń część (gdy quantity=0)
    @Operation(summary = "Delete part", description = "Delete a part if its quantity is zero")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Part deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Part not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete part with non-zero quantity")
    })
    @DeleteMapping("/{materialNumber}/{serialNumber}/{supplierNumber}")
    public ResponseEntity<Void> deletePart(
            @Parameter(description = "Material number", example = "MAT100") @PathVariable String materialNumber,
            @Parameter(description = "Serial number", example = "SER100") @PathVariable String serialNumber,
            @Parameter(description = "Supplier number", example = "SUP100") @PathVariable String supplierNumber) {
        partService.deletePart(materialNumber, serialNumber, supplierNumber);
        return ResponseEntity.noContent().build();
    }

    // PATCH endpoint to increase the quantity of a part
    // PATCH /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}/add?amount=X
    @Operation(summary = "Increase part quantity", description = "Increase the quantity of a part by a given amount")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity increased"),
            @ApiResponse(responseCode = "404", description = "Part not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amount")
    })
    @PatchMapping("/{materialNumber}/{serialNumber}/{supplierNumber}/add")
    public Part increaseQuantity(
            @Parameter(description = "Material number", example = "MAT100") @PathVariable String materialNumber,
            @Parameter(description = "Serial number", example = "SER100") @PathVariable String serialNumber,
            @Parameter(description = "Supplier number", example = "SUP100") @PathVariable String supplierNumber,
            @Parameter(description = "Amount to add", example = "10") @RequestParam int amount) {
        return partService.increaseQuantity(materialNumber, serialNumber, supplierNumber, amount);
    }

    // PATCH endpoint to decrease the quantity of a part (never below zero)
    // PATCH /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}/subtract?amount=X
    @Operation(summary = "Decrease part quantity", description = "Decrease the quantity of a part by a given amount")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity decreased"),
            @ApiResponse(responseCode = "404", description = "Part not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "409", description = "Cannot decrease below zero")
    })
    @PatchMapping("/{materialNumber}/{serialNumber}/{supplierNumber}/subtract")
    public Part decreaseQuantity(
            @Parameter(description = "Material number", example = "MAT100") @PathVariable String materialNumber,
            @Parameter(description = "Serial number", example = "SER100") @PathVariable String serialNumber,
            @Parameter(description = "Supplier number", example = "SUP100") @PathVariable String supplierNumber,
            @Parameter(description = "Amount to subtract", example = "5") @RequestParam int amount) {
        return partService.decreaseQuantity(materialNumber, serialNumber, supplierNumber, amount);
    }
}
