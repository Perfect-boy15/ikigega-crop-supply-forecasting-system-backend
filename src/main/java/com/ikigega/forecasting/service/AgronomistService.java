package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.entity.CropType;
import com.ikigega.forecasting.entity.Warehouse;
import com.ikigega.forecasting.repository.AgronomistRepository;
import com.ikigega.forecasting.repository.CropTypeRepository;
import com.ikigega.forecasting.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgronomistService {

    private final AgronomistRepository agronomistRepository;
    private final CropTypeRepository cropTypeRepository;
    private final WarehouseRepository warehouseRepository;

    public AgronomistService(AgronomistRepository agronomistRepository,
                             CropTypeRepository cropTypeRepository,
                             WarehouseRepository warehouseRepository) {
        this.agronomistRepository = agronomistRepository;
        this.cropTypeRepository = cropTypeRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public String save(Agronomist agronomist) {
        agronomistRepository.save(agronomist);
        return "Agronomist saved successfully";
    }

    public List<Agronomist> findAll() {
        return agronomistRepository.findAll();
    }

    public Optional<Agronomist> findById(UUID id) {
        return agronomistRepository.findById(id);
    }

    public String update(UUID id, Agronomist agronomist) {
        Optional<Agronomist> existing = agronomistRepository.findById(id);
        if (existing.isPresent()) {
            Agronomist agronomistToUpdate = existing.get();
            agronomistToUpdate.setFirstName(agronomist.getFirstName());
            agronomistToUpdate.setLastName(agronomist.getLastName());
            agronomistToUpdate.setPhone(agronomist.getPhone());
            agronomistToUpdate.setWarehouse(agronomist.getWarehouse());
            agronomistToUpdate.setCropTypes(agronomist.getCropTypes());

            agronomistRepository.save(agronomistToUpdate);
            return "Agronomist updated successfully";
        }
        return "The agronomist with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<Agronomist> existing = agronomistRepository.findById(id);
        if (existing.isPresent()) {
            agronomistRepository.delete(existing.get());
            return "Agronomist deleted successfully";
        }
        return "The agronomist with that id does not exist";
    }

    public List<Agronomist> findByCropType(String name) {
        return agronomistRepository.findByCropType(name);
    }

    @Transactional
    public String addCropType(UUID agronomistId, UUID cropTypeId) {
        Optional<Agronomist> agronomistOptional = agronomistRepository.findById(agronomistId);
        if (agronomistOptional.isEmpty()) {
            return "The agronomist with that id does not exist";
        }
        Optional<CropType> cropTypeOptional = cropTypeRepository.findById(cropTypeId);
        if (cropTypeOptional.isEmpty()) {
            return "That crop type does not exist";
        }
        Agronomist agronomist = agronomistOptional.get();
        if (agronomist.getCropTypes() == null) {
            agronomist.setCropTypes(new ArrayList<>());
        }
        CropType cropType = cropTypeOptional.get();
        if (agronomist.getCropTypes().contains(cropType)) {
            return "The agronomist already has that crop type";
        }
        agronomist.getCropTypes().add(cropType);
        agronomistRepository.save(agronomist);
        return "Crop type added to the agronomist";
    }

    @Transactional
    public String changeWarehouse(UUID agronomistId, int warehouseNumber) {
        Optional<Agronomist> agronomistOptional = agronomistRepository.findById(agronomistId);
        if (agronomistOptional.isEmpty()) {
            return "The agronomist with that id does not exist";
        }
        List<Warehouse> warehouses = warehouseRepository.findByWarehouseNumber(warehouseNumber);
        if (warehouses.isEmpty()) {
            return "The warehouse with that warehouseNumber does not exist";
        }
        Warehouse newWarehouse = warehouses.get(0);
        Agronomist agronomist = agronomistOptional.get();
        if (agronomist.getWarehouse() != null
                && newWarehouse.getId() != null
                && agronomist.getWarehouse().getId() != null
                && agronomist.getWarehouse().getId().equals(newWarehouse.getId())) {
            return "Warehouse changed successfully";
        }

        if (agronomistRepository.existsByWarehouse(newWarehouse)) {
            return "That warehouse is taken by another agronomist";
        }
        agronomist.setWarehouse(newWarehouse);
        agronomistRepository.save(agronomist);
        return "Warehouse changed successfully";
    }
}
