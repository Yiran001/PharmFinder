package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderPharmacyRepository;
import com.pharm.pharmfinder.model.PharmFinderAddress;
import com.pharm.pharmfinder.model.PharmacyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.*;

@Controller
@RequestMapping(path="/address")
public class PharmFinderAddressController {

    @Autowired
    private PharmFinderAddressRepository pharmFinderAddressRepository;

    @PostMapping(path = "/add")
    public @ResponseBody String addNewAddress(@RequestParam String street, @RequestParam String houseNumber,@RequestParam String postcode){
        PharmFinderAddress address = new PharmFinderAddress();

        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostcode(postcode);

        pharmFinderAddressRepository.save(address);

        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<PharmFinderAddress> getAllPharmFinderAddresses() {
        // This returns a JSON or XML with the users
        return pharmFinderAddressRepository.findAll();
    }


}
