package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.PharmFinderMedicine;
import com.pharm.pharmfinder.model.PharmFinderMedicineForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/medicine")
public class PharmFinderMedicineController {

    @Autowired
    private PharmFinderMedicineRepository pharmFinderMedicineRepository;

    @RequestMapping(path = "/register")
    public @ResponseBody String registerNewMedicine(@RequestParam String pzn, @RequestParam long amount, @RequestParam String friendlyName, @RequestParam PharmFinderMedicineForm medicineForm){
        //TODO implement
        return "Saved";
    }

}
