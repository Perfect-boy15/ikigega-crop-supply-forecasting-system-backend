package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.service.AgronomistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/agronomists")
public class AgronomistController {

    private final AgronomistService agronomistService;

    public AgronomistController(AgronomistService agronomistService) {
        this.agronomistService = agronomistService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Agronomist agronomist) {
        String message = agronomistService.save(agronomist);
        return ResponseEntity.status(201).body(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Agronomist>> findAll() {
        return ResponseEntity.status(200).body(agronomistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<Agronomist> agronomist = agronomistService.findById(id);
        if (agronomist.isPresent()) {
            return ResponseEntity.status(200).body(agronomist.get());
        }
        return ResponseEntity.status(404).body("The agronomist with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Agronomist agronomist) {
        String message = agronomistService.update(id, agronomist);
        if (message.equals("The agronomist with that id does not exist")) {
            return ResponseEntity.status(404).body("The agronomist with that id does not exist");
        }
        return ResponseEntity.status(200).body(message);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = agronomistService.delete(id);
        if (message.equals("The agronomist with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }

    @GetMapping("/by-crop-type")
    public ResponseEntity<List<Agronomist>> findByCropType(@RequestParam String name) {
        return ResponseEntity.status(200).body(agronomistService.findByCropType(name));
    }

    @PutMapping("/{agronomistId}/crop-types/{cropTypeId}")
    public ResponseEntity<String> addCropType(@PathVariable UUID agronomistId, @PathVariable UUID cropTypeId) {
        String message = agronomistService.addCropType(agronomistId, cropTypeId);
        if (message.equals("The agronomist with that id does not exist") || message.equals("That crop type does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        if (message.equals("The agronomist already has that crop type")) {
            return ResponseEntity.status(409).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }

    @PatchMapping("/{agronomistId}/warehouse")
    public ResponseEntity<String> changeWarehouse(@PathVariable UUID agronomistId, @RequestParam int warehouseNumber) {
        String message = agronomistService.changeWarehouse(agronomistId, warehouseNumber);
        if (message.equals("The agronomist with that id does not exist") || message.equals("The warehouse with that warehouseNumber does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        if (message.equals("That warehouse is taken by another agronomist")) {
            return ResponseEntity.status(409).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }
}
