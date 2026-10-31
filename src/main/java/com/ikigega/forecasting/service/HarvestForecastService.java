package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.repository.HarvestForecastRepository;
import com.ikigega.forecasting.repository.WeighingTicketRepository;
import com.ikigega.forecasting.enums.ForecastStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HarvestForecastService {

    private final HarvestForecastRepository harvestForecastRepository;
    private final WeighingTicketRepository weighingTicketRepository;

    public HarvestForecastService(HarvestForecastRepository harvestForecastRepository,
                                  WeighingTicketRepository weighingTicketRepository) {
        this.harvestForecastRepository = harvestForecastRepository;
        this.weighingTicketRepository = weighingTicketRepository;
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
        if (existing.isEmpty()) {
            return "The harvest forecast with that id does not exist";
        }
        if (weighingTicketRepository.existsByHarvestForecast(existing.get())) {
            return "That harvest forecast has weighing tickets, delete them first";
        }
        harvestForecastRepository.delete(existing.get());
        return "Harvest forecast deleted successfully";
    }

    public List<Object[]> getStatsByStatus() {
        return harvestForecastRepository.findForecastStatsByStatus();
    }

    @Transactional
    public String cancelDay(UUID agronomistId, LocalDate date) {
        int affected = harvestForecastRepository.cancelDay(agronomistId, date, ForecastStatus.DELIVERED, ForecastStatus.WITHDRAWN);
        return affected + " harvest forecasts cancelled";
    }
}
