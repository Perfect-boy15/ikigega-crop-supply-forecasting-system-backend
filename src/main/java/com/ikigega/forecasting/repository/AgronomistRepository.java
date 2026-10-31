package com.ikigega.forecasting.repository;

import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgronomistRepository extends JpaRepository<Agronomist, UUID> {

    @Query("SELECT DISTINCT a FROM Agronomist a JOIN a.cropTypes c WHERE LOWER(c.name) = LOWER(:name)")
    List<Agronomist> findByCropType(@Param("name") String name);

    boolean existsByWarehouse(Warehouse warehouse);

    boolean existsByWarehouseId(UUID warehouseId);
}
