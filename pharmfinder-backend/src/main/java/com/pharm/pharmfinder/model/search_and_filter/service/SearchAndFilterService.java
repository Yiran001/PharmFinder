package com.pharm.pharmfinder.model.search_and_filter.service;

import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.*;
import com.pharm.pharmfinder.model.search_and_filter.MedicineView;
import com.pharm.pharmfinder.model.search_and_filter.SearchAndFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchAndFilterService {

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
            pharmacyMedicines = sortByAmount(pharmacyMedicines, request.isDescending());
        List<Medicine> medicines = getAllMedicinesFromPharmacyMedicines(pharmacyMedicines);
//        search and filter for other parameters
        if (request.getSortBy() != null)
            medicines = sortByOtherParameters(medicines, request.getSortBy(), request.isDescending());
//        search for other parameters
        if (request.getPzn() != null)
            medicines = matchByPzn(medicines, request.getPzn());
//        partial match is allowed<
        if (request.getFriendlyName() != null)
            medicines = matchByFriendlyName(medicines, request.getFriendlyName());
        if (request.getMedicineForm() != null)
            medicines = matchByMedicineForm(medicines, MedicineForm.valueOf(request.getMedicineForm()));

        return map(medicines, userRepository.findByUsername(username));
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

    private List<PharmacyMedicine> getAllPharmacyMedicinesFromPharmacist(String username){
        Pharmacy pharmacy = pharmacyRepository.findByUser(userRepository.findByUsername(username));
        return  new ArrayList<>(pharmacy.getPharmacyMedicines());
    }

    private List<Medicine> getAllMedicinesFromPharmacyMedicines(List<PharmacyMedicine> pharmacyMedicines){
        List<Medicine> medicines = new ArrayList<>();
        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines){
            Medicine oldMedicine = pharmacyMedicine.getMedicine();
            Medicine newMedicine = new Medicine();
            newMedicine.setPzn(oldMedicine.getPzn());
            newMedicine.setFriendlyName(oldMedicine.getFriendlyName());
            newMedicine.setMedicineForm(oldMedicine.getMedicineForm());
            Set<PharmacyMedicine> tempSet = new HashSet<>();
            tempSet.add(pharmacyMedicine);
            newMedicine.setPharmacyMedicines(tempSet);
            medicines.add(newMedicine);
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

    private List<Medicine> sortByOtherParameters(List<Medicine> medicines, String sortBy, boolean descending){
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
//            medicines.sort(Comparator.comparing(Medicine::getMedicineForm));
            medicines.sort(new Comparator<Medicine>() {
                @Override
                public int compare(Medicine o1, Medicine o2) {
                    return String.valueOf(o1.getMedicineForm()).compareTo(String.valueOf(o2.getMedicineForm()));
                }
            });
        }
        if (descending)
            Collections.reverse(medicines);
        return medicines;
    }

    private List<PharmacyMedicine> sortByAmount(List<PharmacyMedicine> medicines, boolean descending){
        medicines.sort(new Comparator<PharmacyMedicine>() {
            @Override
            public int compare(PharmacyMedicine o1, PharmacyMedicine o2) {
                return Long.compare(o1.getAmount(), o2.getAmount());
            }
        });
        if (descending)
            Collections.reverse(medicines);
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
