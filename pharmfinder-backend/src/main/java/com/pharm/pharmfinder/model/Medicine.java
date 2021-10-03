package com.pharm.pharmfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    private String pzn;

    @JsonIgnore
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PharmacyMedicine> pharmacyMedicines = new HashSet<>();

    private String friendlyName;

    private MedicineForm medicineForm;

    public Medicine() {
    }

    public Medicine(String pzn, Set<PharmacyMedicine> pharmacyMedicines, String friendlyName, MedicineForm medicineForm) {
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

    public Set<PharmacyMedicine> getPharmacyMedicines() {
        return pharmacyMedicines;
    }

    public void setPharmacyMedicines(Set<PharmacyMedicine> pharmacyMedicines) {
        this.pharmacyMedicines = pharmacyMedicines;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public MedicineForm getMedicineForm() {
        return medicineForm;
    }

    public void setMedicineForm(MedicineForm medicineForm) {
        this.medicineForm = medicineForm;
    }
}


