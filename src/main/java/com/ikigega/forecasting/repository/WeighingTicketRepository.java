package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.entity.WeighingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WeighingTicketRepository extends JpaRepository<WeighingTicket, UUID> {

    boolean existsByHarvestForecast(HarvestForecast harvestForecast);

    @Query("SELECT w FROM WeighingTicket w WHERE w.harvestForecast.farmer.farmerCode = :farmerCode ORDER BY w.weighedDate DESC")
    List<WeighingTicket> findByFarmerCode(@Param("farmerCode") String farmerCode);
}
