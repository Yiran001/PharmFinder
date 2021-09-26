package com.pharm.pharmfinder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharm.pharmfinder.model.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userID;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_fk")
    @JsonBackReference
    private Address userAddress;

    private String username;
    private String email;
    @JsonIgnore
    private String passwordHash;
    private boolean isPharmacist;
    private boolean enabled;
//    mutliple authorities are comma seperated
    private String  authorities;

    public User() {
    }

    public Address getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Address userAddress) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userAddress=" + userAddress +
                ", username='" + username + '\'' + "\n" +
                ", email='" + email + '\'' + "\n" +
                ", isPharmacist=" + isPharmacist + "\n" +
                ", enabled=" + enabled + "\n" +
                ", authorities='" + authorities + '\'' + "\n" +
                '}';
    }
}
