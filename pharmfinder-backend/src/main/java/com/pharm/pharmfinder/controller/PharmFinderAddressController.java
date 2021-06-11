package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.model.PharmFinderAddress;
import com.pharm.pharmfinder.model.PharmacyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/address")
public class PharmFinderAddressController {

    @Autowired
    private PharmFinderAddressRepository pharmFinderAddressRepository;


    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<PharmFinderAddress> getAllPharmFinderAddresses() {
        // This returns a JSON or XML with the users
        return pharmFinderAddressRepository.findAll();
    }
}
