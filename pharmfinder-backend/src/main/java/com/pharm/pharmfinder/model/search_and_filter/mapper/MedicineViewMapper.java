package com.pharm.pharmfinder.model.search_and_filter.mapper;

import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.model.Medicine;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.PharmacyMedicine;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.search_and_filter.MedicineView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class MedicineViewMapper {

    private PharmacyRepository pharmacyRepository;

    @Autowired
    public void setPharmacyRepository(PharmacyRepository pharmacyRepository){
        this.pharmacyRepository = pharmacyRepository;
    }

//    public abstract ArrayList<MedicineView> toMedicinceView(List<Medicine> medicines, User user);
    public abstract ArrayList<MedicineView> toMedicinceView(List<Medicine> medicines);

//    @AfterMapping
//    protected void after(User user, Medicine medicine, @MappingTarget MedicineView medicineView) {
//        Pharmacy pharmacy = pharmacyRepository.findByUser(user);
//        for (PharmacyMedicine pharmacyMedicine : medicine.getPharmacyMedicines()){
//            if (pharmacyMedicine.getPharmacy().getPharmacyID() == pharmacy.getPharmacyID()){
//                if (Objects.equals(pharmacyMedicine.getMedicine().getPzn(), medicine.getPzn())){
//                    medicineView.setAmount(pharmacyMedicine.getAmount());
//                    break;
//                }
//            }
//        }
//        throw new IllegalStateException("Medicine not registered for this pharmacist");
//    }
}
