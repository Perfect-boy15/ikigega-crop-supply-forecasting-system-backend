package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.enums.ForecastStatus;
import com.ikigega.forecasting.service.HarvestForecastService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        String message = harvestForecastService.save(harvestForecast);
        if (message.equals("Harvest forecast date cannot be in the past")) {
            return ResponseEntity.status(400).body(message);
        }
        if (message.equals("Agronomist already has a forecast visit on that date")) {
            return ResponseEntity.status(409).body(message);
        }
        return ResponseEntity.status(201).body(message);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<HarvestForecast>> findByStatus(@RequestParam ForecastStatus status) {
        return ResponseEntity.status(200).body(harvestForecastService.findByStatus(status));
    }

    @GetMapping("/between")
    public ResponseEntity<List<HarvestForecast>> findBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return ResponseEntity.status(200).body(harvestForecastService.findBetween(startDate, endDate));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam ForecastStatus status) {
        String message = harvestForecastService.updateStatus(id, status);
        if (message.equals("The harvest forecast with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        if (message.startsWith("Cannot change status from")) {
            return ResponseEntity.status(400).body(message);
        }
        return ResponseEntity.status(200).body(message);
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
        String message = harvestForecastService.update(id, harvestForecast);
        if (message.equals("The harvest forecast with that id does not exist")) {
            return ResponseEntity.status(404).body("The harvest forecast with that id does not exist");
        }
        return ResponseEntity.status(200).body(message);
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

    @GetMapping("/page")
    public ResponseEntity<Page<HarvestForecast>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "forecastDate,desc") String sort) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "forecastDate");
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            Sort.Direction sortDirection = Sort.Direction.DESC;
            if (parts.length > 1 && parts[1].equalsIgnoreCase("asc")) {
                sortDirection = Sort.Direction.ASC;
            }
            sortOrder = Sort.by(sortDirection, parts[0]);
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return ResponseEntity.status(200).body(harvestForecastService.getPage(pageable));
    }
}
