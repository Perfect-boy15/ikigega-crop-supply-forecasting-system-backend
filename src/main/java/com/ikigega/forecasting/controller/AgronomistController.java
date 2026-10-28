package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.service.AgronomistService;
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
@RequestMapping("/api/agronomists")
public class AgronomistController {

    private final AgronomistService agronomistService;

    public AgronomistController(AgronomistService agronomistService) {
        this.agronomistService = agronomistService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Agronomist agronomist) {
        agronomistService.save(agronomist);
        return ResponseEntity.status(201).body("Agronomist saved successfully");
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
        Agronomist updated = agronomistService.update(id, agronomist);
        if (updated == null) {
            return ResponseEntity.status(404).body("The agronomist with that id does not exist");
        }
        return ResponseEntity.status(200).body("Agronomist updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = agronomistService.delete(id);
        if (message.equals("The agronomist with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }
}
