package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.CropType;
import com.ikigega.forecasting.repository.CropTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CropTypeService {

    private final CropTypeRepository cropTypeRepository;

    public CropTypeService(CropTypeRepository cropTypeRepository) {
        this.cropTypeRepository = cropTypeRepository;
    }

    public CropType save(CropType cropType) {
        return cropTypeRepository.save(cropType);
    }

    public List<CropType> findAll() {
        return cropTypeRepository.findAll();
    }

    public Optional<CropType> findById(UUID id) {
        return cropTypeRepository.findById(id);
    }

    public CropType update(UUID id, CropType cropType) {
        Optional<CropType> existing = cropTypeRepository.findById(id);
        if (existing.isPresent()) {
            CropType cropTypeToUpdate = existing.get();
            cropTypeToUpdate.setName(cropType.getName());
            return cropTypeRepository.save(cropTypeToUpdate);
        }
        return null;
    }

    public String delete(UUID id) {
        Optional<CropType> existing = cropTypeRepository.findById(id);
        if (existing.isPresent()) {
            cropTypeRepository.delete(existing.get());
            return "Crop type deleted successfully";
        }
        return "The crop type with that id does not exist";
    }
}
