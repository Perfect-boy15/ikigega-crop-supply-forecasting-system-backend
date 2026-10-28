package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.Warehouse;
import com.ikigega.forecasting.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return ResponseEntity.status(201).body("Warehouse saved successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Warehouse>> findAll() {
        return ResponseEntity.status(200).body(warehouseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<Warehouse> warehouse = warehouseService.findById(id);
        if (warehouse.isPresent()) {
            return ResponseEntity.status(200).body(warehouse.get());
        }
        return ResponseEntity.status(404).body("The warehouse with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Warehouse warehouse) {
        Warehouse updated = warehouseService.update(id, warehouse);
        if (updated == null) {
            return ResponseEntity.status(404).body("The warehouse with that id does not exist");
        }
        return ResponseEntity.status(200).body("Warehouse updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = warehouseService.delete(id);
        if (message.equals("The warehouse with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }
}
