package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.User;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Integer> {
}
