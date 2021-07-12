package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/medicines")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

}
