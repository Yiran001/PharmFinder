package com.pharm.pharmfinder.model;

import javax.persistence.*;

@Entity
@Table(name="pharmacy_medicine")
public class PharmFinderPharmacyMedicine {

    @EmbeddedId
    private PharmFinderPharmacyMedicineId id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PharmFinderPharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    private PharmFinderMedicine medicine;

    private long amount;

    public PharmFinderPharmacyMedicine() {
    }

    public PharmFinderPharmacyMedicine(PharmFinderPharmacy pharmacy, PharmFinderMedicine medicine, long amount) {
        this.pharmacy = pharmacy;
        this.medicine = medicine;
        this.amount = amount;
    }

    public PharmFinderPharmacyMedicineId getId() {
        return id;
    }

    public void setId(PharmFinderPharmacyMedicineId id) {
        this.id = id;
    }

    public PharmFinderPharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(PharmFinderPharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public PharmFinderMedicine getMedicine() {
        return medicine;
    }

    public void setMedicine(PharmFinderMedicine medicine) {
        this.medicine = medicine;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
