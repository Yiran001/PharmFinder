package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.PharmacyUser;
import org.springframework.data.repository.CrudRepository;

public interface PharmFinderUserRepository extends CrudRepository<PharmacyUser, Integer> {

}

