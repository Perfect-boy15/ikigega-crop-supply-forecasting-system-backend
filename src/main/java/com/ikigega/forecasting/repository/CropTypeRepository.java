package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CropTypeRepository extends JpaRepository<CropType, UUID> {

    @Query("SELECT c FROM CropType c WHERE c.agronomists IS EMPTY")
    List<CropType> findUnused();
}
