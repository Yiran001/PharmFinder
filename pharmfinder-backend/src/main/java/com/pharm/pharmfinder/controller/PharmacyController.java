package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyRepository pharmacyRepository;


}