package com.pharm.pharmfinder.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="pharmacy")
public class PharmFinderPharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pharmacyID;

    @ManyToOne(fetch = FetchType.LAZY)
    private PharmFinderAddress pharmacyAddress;

    @OneToOne
    private PharmacyUser owner;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PharmFinderPharmacyMedicine> pharmacyMedicines = new HashSet<>();

    private String pharmacyName;

    public PharmFinderPharmacy(int pharmacyID, PharmFinderAddress pharmacyAddress, PharmacyUser owner, String pharmacyName) {
        this.pharmacyID = pharmacyID;
        this.pharmacyAddress = pharmacyAddress;
        this.owner = owner;
        this.pharmacyName = pharmacyName;
    }

    public int getPharmacyID() {
        return pharmacyID;
    }

    public PharmacyUser getOwner() {
        return owner;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public PharmFinderPharmacy() {
    }

    public void setPharmacyID(int pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    public PharmFinderAddress getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(PharmFinderAddress pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    public void setOwner(PharmacyUser owner) {
        this.owner = owner;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }
}
