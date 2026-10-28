package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.repository.HarvestForecastRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HarvestForecastService {

    private final HarvestForecastRepository harvestForecastRepository;

    public HarvestForecastService(HarvestForecastRepository harvestForecastRepository) {
        this.harvestForecastRepository = harvestForecastRepository;
    }

    public HarvestForecast save(HarvestForecast harvestForecast) {
        return harvestForecastRepository.save(harvestForecast);
    }

    public List<HarvestForecast> findAll() {
        return harvestForecastRepository.findAll();
    }

    public Optional<HarvestForecast> findById(UUID id) {
        return harvestForecastRepository.findById(id);
    }

    public HarvestForecast update(UUID id, HarvestForecast harvestForecast) {
        Optional<HarvestForecast> existing = harvestForecastRepository.findById(id);
        if (existing.isPresent()) {
            HarvestForecast forecastToUpdate = existing.get();
            forecastToUpdate.setForecastDate(harvestForecast.getForecastDate());
            forecastToUpdate.setCropName(harvestForecast.getCropName());
            forecastToUpdate.setExpectedKg(harvestForecast.getExpectedKg());
            return harvestForecastRepository.save(forecastToUpdate);
        }
        return null;
    }

    public String delete(UUID id) {
        Optional<HarvestForecast> existing = harvestForecastRepository.findById(id);
        if (existing.isPresent()) {
            harvestForecastRepository.delete(existing.get());
            return "Harvest forecast deleted successfully";
        }
        return "The harvest forecast with that id does not exist";
    }
}
