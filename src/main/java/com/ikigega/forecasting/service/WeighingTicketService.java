package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.entity.WeighingTicket;
import com.ikigega.forecasting.repository.HarvestForecastRepository;
import com.ikigega.forecasting.repository.WeighingTicketRepository;
import com.ikigega.forecasting.enums.ForecastStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeighingTicketService {

    private final WeighingTicketRepository weighingTicketRepository;
    private final HarvestForecastRepository harvestForecastRepository;

    public WeighingTicketService(WeighingTicketRepository weighingTicketRepository,
                                 HarvestForecastRepository harvestForecastRepository) {
        this.weighingTicketRepository = weighingTicketRepository;
        this.harvestForecastRepository = harvestForecastRepository;
    }

    public String save(WeighingTicket weighingTicket, UUID harvestForecastId) {
        Optional<HarvestForecast> existing = harvestForecastRepository.findById(harvestForecastId);
        if (existing.isEmpty()) {
            return "The harvest forecast with that id does not exist";
        }
        HarvestForecast harvestForecast = existing.get();
        if (harvestForecast.getStatus() != ForecastStatus.DELIVERED) {
            return "Weighing tickets are only for DELIVERED harvest forecasts";
        }
        weighingTicket.setWeighedDate(LocalDate.now());
        weighingTicket.setHarvestForecast(harvestForecast);
        weighingTicketRepository.save(weighingTicket);
        return "Weighing ticket saved successfully";
    }

    public List<WeighingTicket> findAll() {
        return weighingTicketRepository.findAll();
    }

    public Optional<WeighingTicket> findById(UUID id) {
        return weighingTicketRepository.findById(id);
    }

    public String update(UUID id, WeighingTicket weighingTicket) {
        Optional<WeighingTicket> existing = weighingTicketRepository.findById(id);
        if (existing.isPresent()) {
            WeighingTicket weighingTicketToUpdate = existing.get();
            weighingTicketToUpdate.setProduceName(weighingTicket.getProduceName());
            weighingTicketToUpdate.setRemarks(weighingTicket.getRemarks());
            weighingTicketToUpdate.setWeightKg(weighingTicket.getWeightKg());
            weighingTicketToUpdate.setWeighedDate(weighingTicket.getWeighedDate());
            weighingTicketToUpdate.setHarvestForecast(weighingTicket.getHarvestForecast());

            weighingTicketRepository.save(weighingTicketToUpdate);
            return "Weighing ticket updated successfully";
        }
        return "The weighing ticket with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<WeighingTicket> existing = weighingTicketRepository.findById(id);
        if (existing.isPresent()) {
            weighingTicketRepository.delete(existing.get());
            return "Weighing ticket deleted successfully";
        }
        return "The weighing ticket with that id does not exist";
    }

    public List<WeighingTicket> findByFarmerCode(String farmerCode) {
        return weighingTicketRepository.findByFarmerCode(farmerCode);
    }
}
