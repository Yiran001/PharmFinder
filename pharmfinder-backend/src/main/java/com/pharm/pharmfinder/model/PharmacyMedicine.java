package com.pharm.pharmfinder.model;

import javax.persistence.*;

@Entity
@Table(name = "pharmacy_medicine")
public class PharmacyMedicine {

    @EmbeddedId
    private PharmacyMedicineId id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Medicine medicine;

    private long amount;

    public PharmacyMedicine() {
    }

    public PharmacyMedicine(Pharmacy pharmacy, Medicine medicine, long amount) {
        this.pharmacy = pharmacy;
        this.medicine = medicine;
        this.amount = amount;
    }

    public PharmacyMedicineId getId() {
        return id;
    }

    public void setId(PharmacyMedicineId id) {
        this.id = id;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
