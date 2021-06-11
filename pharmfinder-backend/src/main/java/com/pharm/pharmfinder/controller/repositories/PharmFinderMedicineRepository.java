package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.PharmFinderMedicine;
import org.springframework.data.repository.CrudRepository;

public interface PharmFinderMedicineRepository extends CrudRepository<PharmFinderMedicine, String> {
}
