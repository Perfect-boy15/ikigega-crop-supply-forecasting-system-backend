package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    @Query("SELECT w FROM Warehouse w WHERE NOT EXISTS (SELECT aw FROM Agronomist aw WHERE aw.warehouse = w) ORDER BY w.warehouseNumber")
    List<Warehouse> findFreeWarehouses();

    List<Warehouse> findByWarehouseNumber(int warehouseNumber);
}
