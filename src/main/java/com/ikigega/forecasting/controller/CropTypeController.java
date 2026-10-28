package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.CropType;
import com.ikigega.forecasting.service.CropTypeService;
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
@RequestMapping("/api/crop-types")
public class CropTypeController {

    private final CropTypeService cropTypeService;

    public CropTypeController(CropTypeService cropTypeService) {
        this.cropTypeService = cropTypeService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody CropType cropType) {
        cropTypeService.save(cropType);
        return ResponseEntity.status(201).body("Crop type saved successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<CropType>> findAll() {
        return ResponseEntity.status(200).body(cropTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<CropType> cropType = cropTypeService.findById(id);
        if (cropType.isPresent()) {
            return ResponseEntity.status(200).body(cropType.get());
        }
        return ResponseEntity.status(404).body("The crop type with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody CropType cropType) {
        CropType updated = cropTypeService.update(id, cropType);
        if (updated == null) {
            return ResponseEntity.status(404).body("The crop type with that id does not exist");
        }
        return ResponseEntity.status(200).body("Crop type updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = cropTypeService.delete(id);
        if (message.equals("The crop type with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }
}
