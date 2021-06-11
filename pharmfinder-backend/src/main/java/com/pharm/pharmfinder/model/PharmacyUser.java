package com.pharm.pharmfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;

@Entity
@Table(name="pharmacy_user")
public class PharmacyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userID;

    @ManyToOne
    @JsonIgnore
    private PharmFinderAddress userAddress;

    private String username;
    private String email;
    private String passwordHash;
    private boolean isPharmacist;

    public PharmacyUser(){}

    public PharmFinderAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(PharmFinderAddress userAddress) {
        this.userAddress = userAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPharmacist() {
        return isPharmacist;
    }

    public void setPharmacist(boolean pharmacist) {
        isPharmacist = pharmacist;
    }

    @Override
    public String toString() {
        return "PharmacyUser{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", isPharmacist=" + isPharmacist +
                '}';
    }
}
