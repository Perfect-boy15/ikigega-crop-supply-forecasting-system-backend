package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.Farmer;
import com.ikigega.forecasting.entity.HarvestForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

    List<Farmer> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    @Query("SELECT DISTINCT h.farmer FROM HarvestForecast h WHERE h.agronomist.id = :agronomistId")
    List<Farmer> findFarmersByAgronomistId(@Param("agronomistId") UUID agronomistId);
}
