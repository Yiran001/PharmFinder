package com.pharm.pharmfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="address")
public class PharmFinderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int addressID;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userAddress")
    private Set<PharmacyUser> addressUsers;
    
    private String street;
    private String houseNumber;
    private String postcode;

    public PharmFinderAddress(){}

    public PharmFinderAddress(Set<PharmacyUser> addressUsers, String street, String houseNumber, String postcode) {
        this.addressUsers = addressUsers;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
    }
    public PharmFinderAddress(PharmacyUser addressUser, String street, String houseNumber, String postcode) {
        if(addressUsers == null)
            this.addressUsers = new HashSet<>();
        this.addressUsers.add(addressUser);
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
    }

    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public Set<PharmacyUser> getAddressUsers() {
        return addressUsers;
    }

    public void setAddressUsers(Set<PharmacyUser> addressUsers) {
        this.addressUsers = addressUsers;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String toString() {
        return "PharmFinderAddress{" +
                "addressID=" + addressID +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
