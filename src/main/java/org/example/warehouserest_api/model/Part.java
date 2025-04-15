package org.example.warehouserest_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


// Entity class representing a part in the warehouse inventory.
// Each part has a composite identifier (PartId) and a quantity.
@Entity
@Table(name = "parts")
public class Part {

    // Composite primary key consisting of material number, serial number, and supplier number
    @EmbeddedId
    private PartId id;

    // Current quantity of this part in the warehouse.
    // Cannot be negative.
    @NotNull
    @Column(nullable = false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    // Default constructor required by JPA
    public Part() {
    }

    // Parameterized constructor to create a part with specified ID and quantity
    public Part(PartId id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    // Getters and setters
    public PartId getId() {
        return id;
    }

    public void setId(PartId id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
