package com.ikigega.forecasting.service;

import com.ikigega.forecasting.entity.Agronomist;
import com.ikigega.forecasting.repository.AgronomistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgronomistService {

    private final AgronomistRepository agronomistRepository;

    public AgronomistService(AgronomistRepository agronomistRepository) {
        this.agronomistRepository = agronomistRepository;
    }

    public Agronomist save(Agronomist agronomist) {
        return agronomistRepository.save(agronomist);
    }

    public List<Agronomist> findAll() {
        return agronomistRepository.findAll();
    }

    public Optional<Agronomist> findById(UUID id) {
        return agronomistRepository.findById(id);
    }

    public Agronomist update(UUID id, Agronomist agronomist) {
        Optional<Agronomist> existing = agronomistRepository.findById(id);
        if (existing.isPresent()) {
            Agronomist agronomistToUpdate = existing.get();
            agronomistToUpdate.setFirstName(agronomist.getFirstName());
            agronomistToUpdate.setLastName(agronomist.getLastName());
            agronomistToUpdate.setPhone(agronomist.getPhone());
            agronomistToUpdate.setWarehouse(agronomist.getWarehouse());
            return agronomistRepository.save(agronomistToUpdate);
        }
        return null;
    }

    public String delete(UUID id) {
        Optional<Agronomist> existing = agronomistRepository.findById(id);
        if (existing.isPresent()) {
            agronomistRepository.delete(existing.get());
            return "Agronomist deleted successfully";
        }
        return "The agronomist with that id does not exist";
    }
}
