package com.ikigega.forecasting.controller;

import com.ikigega.forecasting.entity.WeighingTicket;
import com.ikigega.forecasting.service.WeighingTicketService;
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
@RequestMapping("/api/weighing-tickets")
public class WeighingTicketController {

    private final WeighingTicketService weighingTicketService;

    public WeighingTicketController(WeighingTicketService weighingTicketService) {
        this.weighingTicketService = weighingTicketService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID harvestForecastId, @RequestBody WeighingTicket weighingTicket) {
        String message = weighingTicketService.save(weighingTicket, harvestForecastId);
        if (message.equals("The harvest forecast with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        if (message.equals("Weighing tickets are only for DELIVERED harvest forecasts")) {
            return ResponseEntity.status(400).body(message);
        }
        return ResponseEntity.status(201).body(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<WeighingTicket>> findAll() {
        return ResponseEntity.status(200).body(weighingTicketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        Optional<WeighingTicket> weighingTicket = weighingTicketService.findById(id);
        if (weighingTicket.isPresent()) {
            return ResponseEntity.status(200).body(weighingTicket.get());
        }
        return ResponseEntity.status(404).body("The weighing ticket with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody WeighingTicket weighingTicket) {
        WeighingTicket updated = weighingTicketService.update(id, weighingTicket);
        if (updated == null) {
            return ResponseEntity.status(404).body("The weighing ticket with that id does not exist");
        }
        return ResponseEntity.status(200).body("Weighing ticket updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = weighingTicketService.delete(id);
        if (message.equals("The weighing ticket with that id does not exist")) {
            return ResponseEntity.status(404).body(message);
        }
        return ResponseEntity.status(200).body(message);
    }

    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<List<WeighingTicket>> findByFarmerCode(@PathVariable String farmerCode) {
        return ResponseEntity.status(200).body(weighingTicketService.findByFarmerCode(farmerCode));
    }
}
