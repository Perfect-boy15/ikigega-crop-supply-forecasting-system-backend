package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.Farmer;
import com.ikigega.forecasting.service.FarmerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/farmers")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Farmer farmer) {
        farmerService.save(farmer);
        return ResponseEntity.status(201).body("Farmer saved successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Farmer>> findAll() {
        return ResponseEntity.status(200).body(farmerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<Farmer> farmer = farmerService.findById(id);
        if (farmer.isPresent()) {
            return ResponseEntity.status(200).body(farmer.get());
        }
        return ResponseEntity.status(404).body("The farmer with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Farmer farmer) {
        Farmer updated = farmerService.update(id, farmer);
        if (updated == null) {
            return ResponseEntity.status(404).body("The farmer with that id does not exist");
        }
        return ResponseEntity.status(200).body("Farmer updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = farmerService.delete(id);
        if (message.equals("The farmer with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }

    @GetMapping("/by-last-name")
    public ResponseEntity<List<Farmer>> findByLastName(@RequestParam String lastName) {
        return ResponseEntity.status(200).body(farmerService.findByLastName(lastName));
    }
}
