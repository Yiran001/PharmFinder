package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.Medicine;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.PharmacyMedicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
@RequestMapping(path = "/search")
public class SearchPharmacyController {
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PharmacyMedicineRepository pharmacyMedicineRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping(path = "/phamacy", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String index(HttpServletRequest request) {
        String username = request.getParameter("pzn");
        checkAuthorization(request, username);

        Iterable<PharmacyMedicine> pharmacyMedicines = getPharmacy(username).getPharmacyMedicines();
        StringBuilder result = new StringBuilder();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines) {
            Medicine medicine = pharmacyMedicine.getMedicine();
            Pharmacy pharmacy = pharmacyMedicine.getPharmacy();
            if (pharmacy.getPharmacyName().equals(username)){
                result.append(medicine.toString());
                result.append(" Amount: ").append(pharmacyMedicine.getAmount());
                result.append("\n");
            }
        }
        if (result.toString().equals(""))
            result.append("No medicines registered");
        return result.toString();
    }

    //jwt authorization
    private boolean matchUsernameAndJwt(String jwt, String username){
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        return !username.equals(jwtUsername);
    }
    private void checkAuthorization(HttpServletRequest request, String username){
        String jwt = request.getHeader("Authorization").substring(7);
        if (matchUsernameAndJwt(jwt, username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong username");
    }
}
