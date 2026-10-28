package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.Agronomist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgronomistRepository extends JpaRepository<Agronomist, UUID> {
}
