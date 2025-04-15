package org.example.warehouserest_api.repository;

import org.example.warehouserest_api.model.Part;
import org.example.warehouserest_api.model.PartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for CRUD operations on Part entities.
// Extends JpaRepository to inherit standard data access methods.
@Repository
public interface PartRepository extends JpaRepository<Part, PartId> {
}
