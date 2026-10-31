package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.service.HarvestForecastService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/harvest-forecasts")
public class HarvestForecastController {

    private final HarvestForecastService harvestForecastService;

    public HarvestForecastController(HarvestForecastService harvestForecastService) {
        this.harvestForecastService = harvestForecastService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody HarvestForecast harvestForecast) {
        harvestForecastService.save(harvestForecast);
        return ResponseEntity.status(201).body("Harvest forecast saved successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<HarvestForecast>> findAll() {
        return ResponseEntity.status(200).body(harvestForecastService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<HarvestForecast> harvestForecast = harvestForecastService.findById(id);
        if (harvestForecast.isPresent()) {
            return ResponseEntity.status(200).body(harvestForecast.get());
        }
        return ResponseEntity.status(404).body("The harvest forecast with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody HarvestForecast harvestForecast) {
        HarvestForecast updated = harvestForecastService.update(id, harvestForecast);
        if (updated == null) {
            return ResponseEntity.status(404).body("The harvest forecast with that id does not exist");
        }
        return ResponseEntity.status(200).body("Harvest forecast updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = harvestForecastService.delete(id);
        if (message.equals("The harvest forecast with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        if (message.equals("That harvest forecast has weighing tickets, delete them first")) {
            return ResponseEntity.status(409).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getStatsByStatus() {
        return ResponseEntity.status(200).body(harvestForecastService.getStatsByStatus());
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID agronomistId, @RequestParam String date) {
        LocalDate forecastDate = LocalDate.parse(date);
        String message = harvestForecastService.cancelDay(agronomistId, forecastDate);
        return ResponseEntity.status(200).body(message);
    }
}
