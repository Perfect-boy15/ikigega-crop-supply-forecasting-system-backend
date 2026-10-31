package com.ikigega.forecasting.service;

import com.ikigega.forecasting.repository.AgronomistRepository;
import com.ikigega.forecasting.repository.FarmerRepository;
import com.ikigega.forecasting.repository.HarvestForecastRepository;
import com.ikigega.forecasting.repository.WarehouseRepository;
import com.ikigega.forecasting.repository.WeighingTicketRepository;
import com.ikigega.forecasting.enums.ForecastStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final FarmerRepository farmerRepository;
    private final AgronomistRepository agronomistRepository;
    private final WarehouseRepository warehouseRepository;
    private final HarvestForecastRepository harvestForecastRepository;
    private final WeighingTicketRepository weighingTicketRepository;

    public DashboardService(FarmerRepository farmerRepository,
                             AgronomistRepository agronomistRepository,
                             WarehouseRepository warehouseRepository,
                             HarvestForecastRepository harvestForecastRepository,
                             WeighingTicketRepository weighingTicketRepository) {
        this.farmerRepository = farmerRepository;
        this.agronomistRepository = agronomistRepository;
        this.warehouseRepository = warehouseRepository;
        this.harvestForecastRepository = harvestForecastRepository;
        this.weighingTicketRepository = weighingTicketRepository;
    }

    public Map<String, Long> getSummary() {
        long farmers = farmerRepository.count();
        long agronomists = agronomistRepository.count();
        long freeWarehouses = warehouseRepository.findFreeWarehouses().size();
        long forecastsToday = harvestForecastRepository.countByForecastDate(LocalDate.now());
        long submitted = harvestForecastRepository.countByStatus(ForecastStatus.SUBMITTED);
        long weighingTickets = weighingTicketRepository.count();

        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("farmers", farmers);
        summary.put("agronomists", agronomists);
        summary.put("freeWarehouses", freeWarehouses);
        summary.put("forecastsToday", forecastsToday);
        summary.put("submitted", submitted);
        summary.put("weighingTickets", weighingTickets);
        return summary;
    }
}
