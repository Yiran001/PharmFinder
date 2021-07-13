package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.Medicine;
import org.springframework.data.repository.CrudRepository;

public interface MedicineRepository extends CrudRepository<Medicine, String> {
}
