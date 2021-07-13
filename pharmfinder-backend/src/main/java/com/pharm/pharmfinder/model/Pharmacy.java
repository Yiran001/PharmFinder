package com.pharm.pharmfinder.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pharmacyID;

    @ManyToOne(fetch = FetchType.LAZY)
    private Address pharmacyAddress;

    @OneToOne
    private User owner;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PharmacyMedicine> pharmacyMedicines = new HashSet<>();

    private String pharmacyName;

    public Pharmacy(int pharmacyID, Address pharmacyAddress, User owner, String pharmacyName) {
        this.pharmacyID = pharmacyID;
        this.pharmacyAddress = pharmacyAddress;
        this.owner = owner;
        this.pharmacyName = pharmacyName;
    }

    public int getPharmacyID() {
        return pharmacyID;
    }

    public User getOwner() {
        return owner;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public Pharmacy() {
    }

    public void setPharmacyID(int pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    public Address getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(Address pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }
}
