package com.pharm.pharmfinder.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="medicine")
public class PharmFinderMedicine {

    @Id
    private String pzn;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PharmFinderPharmacyMedicine> pharmacyMedicines = new HashSet<>();

    private String friendlyName;

    private PharmFinderMedicineForm medicineForm;

    public PharmFinderMedicine() {
    }

    public PharmFinderMedicine(String pzn, Set<PharmFinderPharmacyMedicine> pharmacyMedicines, String friendlyName, PharmFinderMedicineForm medicineForm) {
        this.pzn = pzn;
        this.pharmacyMedicines = pharmacyMedicines;
        this.friendlyName = friendlyName;
        this.medicineForm = medicineForm;
    }

    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    public Set<PharmFinderPharmacyMedicine> getPharmacyMedicines() {
        return pharmacyMedicines;
    }

    public void setPharmacyMedicines(Set<PharmFinderPharmacyMedicine> pharmacyMedicines) {
        this.pharmacyMedicines = pharmacyMedicines;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public PharmFinderMedicineForm getMedicineForm() {
        return medicineForm;
    }

    public void setMedicineForm(PharmFinderMedicineForm medicineForm) {
        this.medicineForm = medicineForm;
    }
}


