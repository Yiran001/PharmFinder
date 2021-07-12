package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.PharmacyMedicine;
import org.springframework.data.repository.CrudRepository;

public interface PharmacyMedicineRepository extends CrudRepository<PharmacyMedicine, Integer> {
}
