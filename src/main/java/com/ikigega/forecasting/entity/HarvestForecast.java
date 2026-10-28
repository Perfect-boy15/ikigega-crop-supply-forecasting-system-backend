package com.ikigega.forecasting.entity;

import com.ikigega.forecasting.enums.ForecastStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "harvest_forecast")
public class HarvestForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate forecastDate;

    @Enumerated(EnumType.STRING)
    private ForecastStatus status;

    private String cropName;

    private int expectedKg;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "agronomist_id")
    private Agronomist agronomist;

    public HarvestForecast() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDate forecastDate) {
        this.forecastDate = forecastDate;
    }

    public ForecastStatus getStatus() {
        return status;
    }

    public void setStatus(ForecastStatus status) {
        this.status = status;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public int getExpectedKg() {
        return expectedKg;
    }

    public void setExpectedKg(int expectedKg) {
        this.expectedKg = expectedKg;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public Agronomist getAgronomist() {
        return agronomist;
    }

    public void setAgronomist(Agronomist agronomist) {
        this.agronomist = agronomist;
    }
}
