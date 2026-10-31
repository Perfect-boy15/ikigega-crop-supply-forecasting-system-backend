package com.ikigega.forecasting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weighing_ticket")
public class WeighingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String produceName;

    private String remarks;

    private int weightKg;

    private LocalDate weighedDate;

    @ManyToOne
    @JoinColumn(name = "harvest_forecast_id")
    private HarvestForecast harvestForecast;

    public WeighingTicket() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduceName() {
        return produceName;
    }

    public void setProduceName(String produceName) {
        this.produceName = produceName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(int weightKg) {
        this.weightKg = weightKg;
    }

    public LocalDate getWeighedDate() {
        return weighedDate;
    }

    public void setWeighedDate(LocalDate weighedDate) {
        this.weighedDate = weighedDate;
    }

    public HarvestForecast getHarvestForecast() {
        return harvestForecast;
    }

    public void setHarvestForecast(HarvestForecast harvestForecast) {
        this.harvestForecast = harvestForecast;
    }
}
