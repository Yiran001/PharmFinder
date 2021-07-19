package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.Medicine;
import com.pharm.pharmfinder.model.MedicineForm;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.PharmacyMedicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Controller
@CrossOrigin
@RequestMapping(path = "/medicines")
public class MedicinesController {

    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PharmacyMedicineRepository pharmacyMedicineRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String create(HttpServletRequest request){
        String pzn = request.getParameter("pzn");
        String friendlyName = request.getParameter("friendlyName");
        String medicineForm = request.getParameter("medicineForm");
        String username = request.getParameter("username");
        String amountString = request.getParameter("amount");
        int amount = Integer.parseInt(amountString);
        checkAuthorization(request, username);

        Medicine medicine = getOrCreateMedicine(pzn, friendlyName, MedicineForm.valueOf(medicineForm));
        Pharmacy pharmacy = getPharmacy(username);
        if (pharmacyMedicineExists(pharmacy, medicine))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicine already registered");
        PharmacyMedicine pharmacyMedicine = new PharmacyMedicine(pharmacy, medicine, amount);
        savePharmacyMedicineToMedicine(medicine, pharmacyMedicine);
        savePharmacyMedicineToPharmacy(pharmacy, pharmacyMedicine);
        pharmacyMedicineRepository.save(pharmacyMedicine);
        medicineRepository.save(medicine);
        pharmacyRepository.save(pharmacy);
        return "Saved";
    }

    @GetMapping(path = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String index(HttpServletRequest request) {
        String username = request.getParameter("username");
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

    @PutMapping(path = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update(HttpServletRequest request){
        String pzn = request.getParameter("pzn");
        String friendlyName = request.getParameter("friendlyName");
        String medicineForm = request.getParameter("medicineForm");
        String username = request.getParameter("username");
        String amountString = request.getParameter("amount");
        int amount = Integer.parseInt(amountString);
        checkAuthorization(request, username);

        Pharmacy pharmacy = getPharmacy(username);
        PharmacyMedicine pharmacyMedicine = getPharmacyMedicine(pzn, pharmacy);
        Medicine medicine = pharmacyMedicine.getMedicine();
        if (amount < 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Amount can not be negative");
        }
        medicine.setFriendlyName(friendlyName);
        medicine.setMedicineForm(MedicineForm.valueOf(medicineForm));
        pharmacyMedicine.setAmount(amount);
        medicineRepository.save(medicine);
        pharmacyMedicineRepository.save(pharmacyMedicine);
        return "Medicine updated";

    }

    @DeleteMapping(path = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String delete(HttpServletRequest request){
        String username = request.getParameter("username");
        String pzn = request.getParameter("pzn");

        Pharmacy pharmacy = getPharmacy(username);
        PharmacyMedicine pharmacyMedicine = getPharmacyMedicine(pzn, pharmacy);
        Medicine medicine = pharmacyMedicine.getMedicine();
        pharmacy.getPharmacyMedicines().remove(pharmacyMedicine);
        medicine.getPharmacyMedicines().remove(pharmacyMedicine);
        pharmacyRepository.save(pharmacy);
        medicineRepository.save(medicine);
        pharmacyMedicineRepository.delete(pharmacyMedicine);
        return "Removed";
    }

    private PharmacyMedicine getPharmacyMedicine(String pzn, Pharmacy pharmacy){
        Set<PharmacyMedicine> pharmacyMedicines = pharmacy.getPharmacyMedicines();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines) {
            if (pharmacyMedicine.getMedicine().getPzn().equals(pzn))
                return pharmacyMedicine;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pzn not found");
    }

    private Pharmacy getPharmacy(String name){
        Pharmacy pharmacy = null;
        for (Pharmacy potentialPharmacy : pharmacyRepository.findAll()) {
            if (potentialPharmacy.getPharmacyName().equals(name)){
                pharmacy = potentialPharmacy;
            }
        }
        if (pharmacy == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacy not found");
        return pharmacy;
    }

    private void savePharmacyMedicineToMedicine(Medicine medicine, PharmacyMedicine pharmacyMedicine){
        Set<PharmacyMedicine> pharmacyMedicines = medicine.getPharmacyMedicines();
        pharmacyMedicines.add(pharmacyMedicine);
        medicine.setPharmacyMedicines(pharmacyMedicines);
    }

    private void savePharmacyMedicineToPharmacy(Pharmacy pharmacy, PharmacyMedicine pharmacyMedicine){
        Set<PharmacyMedicine> pharmacyMedicines = pharmacy.getPharmacyMedicines();
        pharmacyMedicines.add(pharmacyMedicine);
        pharmacy.setPharmacyMedicines(pharmacyMedicines);
    }

    private Medicine getOrCreateMedicine(String pzn, String friendlyName, MedicineForm medicineForm){
        Medicine result = null;
        for(Medicine medicine : medicineRepository.findAll()){
            if (medicine.getPzn().equals(pzn)){
                result = medicine;
                break;
            }
        }
        return Objects.requireNonNullElseGet(result, () -> new Medicine(pzn, new HashSet<PharmacyMedicine>(), friendlyName, medicineForm));
    }

    private boolean pharmacyMedicineExists(Pharmacy pharmacy, Medicine medicine){
        Set<PharmacyMedicine> existing = medicine.getPharmacyMedicines();
        for (PharmacyMedicine pharmacyMedicine : existing) {
            if (pharmacyMedicine.getPharmacy().getPharmacyName().equals(pharmacy.getPharmacyName()))
                return true;
        }
        return false;
    }

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
