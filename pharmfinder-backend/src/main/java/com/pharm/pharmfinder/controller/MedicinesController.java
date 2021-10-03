package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.*;
import com.pharm.pharmfinder.model.search_and_filter.MedicineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String create(HttpServletRequest request) {
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
    public @ResponseBody List<MedicineView> index(HttpServletRequest request) {
        String username = request.getParameter("username");
        checkAuthorization(request, username);

        Iterable<PharmacyMedicine> pharmacyMedicines = getPharmacy(username).getPharmacyMedicines();
        List<Medicine> medicines = new ArrayList<>();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines) {
            medicines.add(pharmacyMedicine.getMedicine());
        }
        User user = userRepository.findByUsername(username);
        return map(medicines, user);
    }

    @PutMapping(path = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update(HttpServletRequest request){
        String pzn = request.getParameter("pzn");
        String friendlyName = request.getParameter("friendlyName");
        String medicineForm = request.getParameter("medicineForm");
        String username = request.getParameter("username");
        String amountString = request.getParameter("amount");
        int amount = Integer.parseInt(amountString);
        boolean admin = checkAuthorization(request, username);

        Pharmacy pharmacy = getPharmacy(username);
        PharmacyMedicine pharmacyMedicine = getPharmacyMedicine(pzn, pharmacy);
        Medicine medicine = pharmacyMedicine.getMedicine();
        if (amount < 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Amount can not be negative");
        }
        if (admin){
            medicine.setFriendlyName(friendlyName);
            medicine.setMedicineForm(MedicineForm.valueOf(medicineForm));
            pharmacyMedicine.setAmount(amount);
            medicineRepository.save(medicine);
        } else {
            pharmacyMedicine.setAmount(amount);
        }
        pharmacyMedicineRepository.save(pharmacyMedicine);
        return "Medicine updated";
    }

    @DeleteMapping(path = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String delete(HttpServletRequest request){
        String username = request.getParameter("username");
        String pzn = request.getParameter("pzn");
        checkAuthorization(request, username);

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

    private List<MedicineView> map(List<Medicine> medicines, User user){
        List<MedicineView> medicineViews = new ArrayList<>();
        for (Medicine medicine : medicines) {
            MedicineView medicineView = new MedicineView();
            medicineView.setPzn(medicine.getPzn());
            medicineView.setFriendlyName(medicine.getFriendlyName());
            medicineView.setMedicineForm(medicine.getMedicineForm());
            Pharmacy pharmacy = pharmacyRepository.findByUser(user);
            for (PharmacyMedicine pharmacyMedicine : medicine.getPharmacyMedicines()) {
                if (pharmacyMedicine.getPharmacy().getPharmacyID() == pharmacy.getPharmacyID()) {
                    if (Objects.equals(pharmacyMedicine.getMedicine().getPzn(), medicine.getPzn())) {
                        medicineView.setAmount(pharmacyMedicine.getAmount());
                        break;
                    }
                }
            }
            medicineViews.add(medicineView);
        }
        return medicineViews;
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

    private boolean checkAuthorization(HttpServletRequest request, String username){
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthority() == Role.MEDICINE_ADMIN) {
            return true;
        }
        if (!username.equals(jwtUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong username");
        }
        checkPharmacist(manipulatingUser);
        return false;
    }

    private void checkPharmacist(User user){
        if (!user.isPharmacist())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not a pharmacist");
    }
}
