package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.HarvestForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HarvestForecastRepository extends JpaRepository<HarvestForecast, UUID> {
}
