package com.pharm.pharmfinder.controller.repositories;


import com.pharm.pharmfinder.model.Pharmacy;
import org.springframework.data.repository.CrudRepository;

public interface PharmacyRepository extends CrudRepository<Pharmacy, Integer> {

}
