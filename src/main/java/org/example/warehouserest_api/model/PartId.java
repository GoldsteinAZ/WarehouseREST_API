package org.example.warehouserest_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

// Embeddable class representing the composite primary key for a Part entity.
// Consists of material number, serial number, and supplier number.
@Embeddable
public class PartId implements Serializable {

    // Material number
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Material Number", example = "MAT100")
    private String materialNumber;

    // Serial number
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Serial Number", example = "SER100")
    private String serialNumber;

    // Supplier number
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Supplier Number", example = "SUP100")
    private String supplierNumber;

    // Default constructor required by JPA
    public PartId() {
    }

    // Parameterized constructor to create a part ID with all components
    public PartId(String materialNumber, String serialNumber, String supplierNumber) {
        this.materialNumber = materialNumber;
        this.serialNumber = serialNumber;
        this.supplierNumber = supplierNumber;
    }

    // Getters and setters
    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    // Implements equals method for proper comparison of composite keys.
    // All three components must match for two PartId objects to be equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartId)) return false;
        PartId partId = (PartId) o;
        return Objects.equals(materialNumber, partId.materialNumber) &&
                Objects.equals(serialNumber, partId.serialNumber) &&
                Objects.equals(supplierNumber, partId.supplierNumber);
    }

    // Generates hash code for the composite key.
    // Must be consistent with equals method for proper HashMap operation.
    @Override
    public int hashCode() {
        return Objects.hash(materialNumber, serialNumber, supplierNumber);
    }

    // Creates a string representation of this PartId
    @Override
    public String toString() {
        return "PartId{" +
                "materialNumber='" + materialNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", supplierNumber='" + supplierNumber + '\'' +
                '}';
    }
}
