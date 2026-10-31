package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.Farmer;
import com.ikigega.forecasting.repository.FarmerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;

    public FarmerService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    public Farmer save(Farmer farmer) {
        return farmerRepository.save(farmer);
    }

    public List<Farmer> findAll() {
        return farmerRepository.findAll();
    }

    public Optional<Farmer> findById(UUID id) {
        return farmerRepository.findById(id);
    }

    public Farmer update(UUID id, Farmer farmer) {
        Optional<Farmer> existing = farmerRepository.findById(id);
        if (existing.isPresent()) {
            Farmer farmerToUpdate = existing.get();
            farmerToUpdate.setFirstName(farmer.getFirstName());
            farmerToUpdate.setLastName(farmer.getLastName());
            farmerToUpdate.setFarmerCode(farmer.getFarmerCode());
            farmerToUpdate.setPhone(farmer.getPhone());
            farmerToUpdate.setDistrict(farmer.getDistrict());
            return farmerRepository.save(farmerToUpdate);
        }
        return null;
    }

    public String delete(UUID id) {
        Optional<Farmer> existing = farmerRepository.findById(id);
        if (existing.isPresent()) {
            farmerRepository.delete(existing.get());
            return "Farmer deleted successfully";
        }
        return "The farmer with that id does not exist";
    }

    public List<Farmer> findByLastName(String lastName) {
        return farmerRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }
}
