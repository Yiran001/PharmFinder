package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.PharmFinderUser;
import org.springframework.data.repository.CrudRepository;

public interface PharmFinderUserRepository extends CrudRepository<PharmFinderUser, Integer> {

}

