package com.pharm.pharmfinder.model.search_and_filter.service;

import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.Medicine;
import com.pharm.pharmfinder.model.MedicineForm;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.PharmacyMedicine;
import com.pharm.pharmfinder.model.search_and_filter.MedicineView;
import com.pharm.pharmfinder.model.search_and_filter.SearchAndFilterRequest;
import com.pharm.pharmfinder.model.search_and_filter.mapper.MedicineViewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchAndFilterService {

    private final MedicineViewMapper medicineViewMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;


    public List<MedicineView> getMedicines(SearchAndFilterRequest request){
        String username = request.getUsername();
        List<PharmacyMedicine> pharmacyMedicines = getAllPharmacyMedicinesFromPharmacist(username);
//        search and filter for amount
        if (request.getAmount() != null)
            pharmacyMedicines = matchByAmount(pharmacyMedicines, Long.parseLong(request.getAmount()));
        if (request.getSortBy() != null && request.getSortBy().equals("amount"))
            pharmacyMedicines = sortByAmount(pharmacyMedicines, request.getSortBy());
        List<Medicine> medicines = getAllMedicinesFromPharmacyMedicines(pharmacyMedicines);
//        filter for other parameters
        if (request.getSortBy() != null)
            medicines = sortByOtherParameters(medicines, request.getSortBy());
//        search for other parameters
        if (request.getPzn() != null)
            medicines = matchByPzn(medicines, request.getPzn());
        if (request.getFriendlyName() != null)
            medicines = matchByFriendlyName(medicines, request.getFriendlyName());
        if (request.getMedicineForm() != null)
            medicines = matchByMedicineForm(medicines, MedicineForm.valueOf(request.getMedicineForm()));

        return medicineViewMapper.toMedicinceView(medicines, userRepository.findByUsername(username));
    }

    private List<PharmacyMedicine> getAllPharmacyMedicinesFromPharmacist(String username){
        Pharmacy pharmacy = pharmacyRepository.findByUser(userRepository.findByUsername(username));
        return  new ArrayList<>(pharmacy.getPharmacyMedicines());
    }

    private List<Medicine> getAllMedicinesFromPharmacyMedicines(List<PharmacyMedicine> pharmacyMedicines){
        List<Medicine> medicines = new ArrayList<>();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines){
            medicines.add(pharmacyMedicine.getMedicine());
        }
        return medicines;
    }

    private List<Medicine> matchByPzn(List<Medicine> medicines, String pzn){
        List<Medicine> matchedMedicines = new ArrayList<>();
        for (Medicine medicine : medicines){
            if (Objects.equals(medicine.getPzn(), pzn))
                matchedMedicines.add(medicine);
        }
        return matchedMedicines;
    }

    private List<Medicine> matchByFriendlyName(List<Medicine> medicines, String friendlyName){
        List<Medicine> matchedMedicines = new ArrayList<>();
        for (Medicine medicine : medicines){
            if (medicine.getFriendlyName().toLowerCase().contains(friendlyName.toLowerCase()))
                matchedMedicines.add(medicine);
        }
        return matchedMedicines;
    }

    private List<Medicine> matchByMedicineForm(List<Medicine> medicines, MedicineForm medicineForm){
        List<Medicine> matchedMedicines = new ArrayList<>();
        for (Medicine medicine : medicines){
            if (medicine.getMedicineForm() == medicineForm)
                matchedMedicines.add(medicine);
        }
        return matchedMedicines;
    }

    private ArrayList<PharmacyMedicine> matchByAmount(List<PharmacyMedicine> pharmacyMedicines, long amount){
        ArrayList<PharmacyMedicine> matchedPharmacyMedicines = new ArrayList<>();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines){
            if (pharmacyMedicine.getAmount() == amount)
                matchedPharmacyMedicines.add(pharmacyMedicine);
        }
        return matchedPharmacyMedicines;
    }

    private List<Medicine> sortByOtherParameters(List<Medicine> medicines, String sortBy){
        if (sortBy.equalsIgnoreCase("pzn")){
            medicines.sort(new Comparator<Medicine>() {
                @Override
                public int compare(Medicine o1, Medicine o2) {
                    return o1.getPzn().compareTo(o2.getPzn());
                }
            });
        }
        else if (sortBy.equalsIgnoreCase("friendlyName")){
            medicines.sort(new Comparator<Medicine>() {
                @Override
                public int compare(Medicine o1, Medicine o2) {
                    return o1.getFriendlyName().compareTo(o2.getFriendlyName());
                }
            });
        }
        else if (sortBy.equalsIgnoreCase("medicineForm")){
            medicines.sort(Comparator.comparing(Medicine::getMedicineForm));
        }
        return medicines;
    }

    private List<PharmacyMedicine> sortByAmount(List<PharmacyMedicine> medicines, String sortBy){
        medicines.sort(new Comparator<PharmacyMedicine>() {
            @Override
            public int compare(PharmacyMedicine o1, PharmacyMedicine o2) {
                return String.valueOf(o1.getAmount()).compareTo(String.valueOf(o2.getAmount()));
            }
        });
        return medicines;
    }


//    private Set<PharmacyMedicine> sortByAmount(Set<PharmacyMedicine> pharmacyMedicines){
//        Collections.sort(pharmacyMedicines, new Comparator<PharmacyMedicine>() {
//            @Override
//            public int compare(PharmacyMedicine o1, PharmacyMedicine o2) {
//                return String.valueOf(o1.getAmount()).compareTo(String.valueOf(o2.getAmount()));
//            }
//        });
//    }

}
