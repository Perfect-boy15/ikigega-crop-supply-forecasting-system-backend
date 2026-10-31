package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.Warehouse;
import com.ikigega.forecasting.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public String save(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
        return "Warehouse saved successfully";
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> findById(UUID id) {
        return warehouseRepository.findById(id);
    }

    public String update(UUID id, Warehouse warehouse) {
        Optional<Warehouse> existing = warehouseRepository.findById(id);
        if (existing.isPresent()) {
            Warehouse warehouseToUpdate = existing.get();
            warehouseToUpdate.setWarehouseNumber(warehouse.getWarehouseNumber());
            warehouseToUpdate.setName(warehouse.getName());

            warehouseRepository.save(warehouseToUpdate);
            return "Warehouse updated successfully";
        }
        return "The warehouse with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<Warehouse> existing = warehouseRepository.findById(id);
        if (existing.isPresent()) {
            warehouseRepository.delete(existing.get());
            return "Warehouse deleted successfully";
        }
        return "The warehouse with that id does not exist";
    }

    public List<Warehouse> findFreeWarehouses() {
        return warehouseRepository.findFreeWarehouses();
    }
}
