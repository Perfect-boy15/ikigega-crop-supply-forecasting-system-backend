package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.repository.HarvestForecastRepository;
import com.ikigega.forecasting.repository.WeighingTicketRepository;
import com.ikigega.forecasting.enums.ForecastStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public String save(HarvestForecast harvestForecast) {
        LocalDate forecastDate = harvestForecast.getForecastDate();
        if (forecastDate == null || forecastDate.isBefore(LocalDate.now())) {
            return "Harvest forecast date cannot be in the past";
        }
        Agronomist agronomist = harvestForecast.getAgronomist();
        if (agronomist != null && agronomist.getId() != null) {
            boolean visitExists = harvestForecastRepository.existsByAgronomistIdAndForecastDateAndStatusNot(
                    agronomist.getId(), forecastDate, ForecastStatus.WITHDRAWN);
            if (visitExists) {
                return "Agronomist already has a forecast visit on that date";
            }
        }
        harvestForecast.setStatus(ForecastStatus.SUBMITTED);
        harvestForecastRepository.save(harvestForecast);
        return "Harvest forecast saved successfully";
    }

    public List<HarvestForecast> findByStatus(ForecastStatus status) {
        return harvestForecastRepository.findByStatusOrderByForecastDateAsc(status);
    }

    public List<HarvestForecast> findBetween(LocalDate start, LocalDate end) {
        return harvestForecastRepository.findByForecastDateBetweenOrderByForecastDateAsc(start, end);
    }

    @Transactional
    public String updateStatus(UUID id, ForecastStatus newStatus) {
        Optional<HarvestForecast> existing = harvestForecastRepository.findById(id);
        if (existing.isEmpty()) {
            return "The harvest forecast with that id does not exist";
        }
        HarvestForecast forecast = existing.get();
        ForecastStatus currentStatus = forecast.getStatus();
        if (currentStatus == ForecastStatus.DELIVERED || currentStatus == ForecastStatus.WITHDRAWN) {
            return "Cannot change status from " + currentStatus + " to " + newStatus;
        }
        if (currentStatus == ForecastStatus.SUBMITTED) {
            if (newStatus != ForecastStatus.VERIFIED && newStatus != ForecastStatus.WITHDRAWN) {
                return "Cannot change status from SUBMITTED to " + newStatus;
            }
        }
        if (currentStatus == ForecastStatus.VERIFIED) {
            if (newStatus != ForecastStatus.DELIVERED && newStatus != ForecastStatus.WITHDRAWN) {
                return "Cannot change status from VERIFIED to " + newStatus;
            }
        }
        forecast.setStatus(newStatus);
        harvestForecastRepository.save(forecast);
        return "Harvest forecast status updated successfully";
    }

    public List<HarvestForecast> findAll() {
        return harvestForecastRepository.findAll();
    }

    public Optional<HarvestForecast> findById(UUID id) {
        return harvestForecastRepository.findById(id);
    }

    public String update(UUID id, HarvestForecast harvestForecast) {
        Optional<HarvestForecast> existing = harvestForecastRepository.findById(id);
        if (existing.isPresent()) {
            HarvestForecast forecastToUpdate = existing.get();
            forecastToUpdate.setForecastDate(harvestForecast.getForecastDate());
            forecastToUpdate.setCropName(harvestForecast.getCropName());
            forecastToUpdate.setExpectedKg(harvestForecast.getExpectedKg());
            forecastToUpdate.setFarmer(harvestForecast.getFarmer());
            forecastToUpdate.setAgronomist(harvestForecast.getAgronomist());

            harvestForecastRepository.save(forecastToUpdate);
            return "Harvest forecast updated successfully";
        }
        return "The harvest forecast with that id does not exist";
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

    public Page<HarvestForecast> getPage(Pageable pageable) {
        return harvestForecastRepository.findAll(pageable);
    }

    @Transactional
    public String cancelDay(UUID agronomistId, LocalDate date) {
        int affected = harvestForecastRepository.cancelDay(agronomistId, date, ForecastStatus.DELIVERED, ForecastStatus.WITHDRAWN);
        return affected + " harvest forecasts cancelled";
    }
}
