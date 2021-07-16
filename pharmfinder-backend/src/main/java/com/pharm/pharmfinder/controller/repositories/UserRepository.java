package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}

