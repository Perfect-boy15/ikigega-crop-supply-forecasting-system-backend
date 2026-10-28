package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CropTypeRepository extends JpaRepository<CropType, UUID> {
}
