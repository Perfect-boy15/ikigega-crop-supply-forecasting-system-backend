package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.HarvestForecast;
import com.ikigega.forecasting.enums.ForecastStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HarvestForecastRepository extends JpaRepository<HarvestForecast, UUID> {

    @Query("SELECT h.status, COUNT(h) FROM HarvestForecast h GROUP BY h.status")
    List<Object[]> findForecastStatsByStatus();

    long countByForecastDate(LocalDate forecastDate);

    long countByStatus(ForecastStatus status);

    List<HarvestForecast> findByStatusOrderByForecastDateAsc(ForecastStatus status);

    List<HarvestForecast> findByForecastDateBetweenOrderByForecastDateAsc(LocalDate start, LocalDate end);

    boolean existsByAgronomistIdAndForecastDateAndStatusNot(UUID agronomistId, LocalDate forecastDate, ForecastStatus statusNot);

    @Modifying
    @Query("UPDATE HarvestForecast h SET h.status = :newStatus WHERE h.agronomist.id = :agronomistId AND h.forecastDate = :date AND h.status <> :deliveredStatus")
    int cancelDay(@Param("agronomistId") UUID agronomistId, @Param("date") LocalDate date, @Param("deliveredStatus") ForecastStatus deliveredStatus, @Param("newStatus") ForecastStatus newStatus);
}
