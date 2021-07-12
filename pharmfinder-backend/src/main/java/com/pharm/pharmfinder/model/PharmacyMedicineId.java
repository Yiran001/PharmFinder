package com.pharm.pharmfinder.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PharmacyMedicineId implements Serializable {

    private int pharmacyId;
    private String pzn;

    public PharmacyMedicineId() {
    }

    public PharmacyMedicineId(int pharmacyId, String pzn) {
        this.pharmacyId = pharmacyId;
        this.pzn = pzn;
    }

    public int getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }
}
