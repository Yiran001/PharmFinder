package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyUserRepository;
import com.pharm.pharmfinder.model.PharmFinderAddress;
import com.pharm.pharmfinder.model.PharmacyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;

@Controller
@RequestMapping(path = "/user")
public class PharmacyUserController {

    @Autowired
    private PharmacyUserRepository pharmacyUserRepository;
    @Autowired
    private PharmFinderAddressRepository pharmFinderAddressRepository;

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username was already taken")
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public void usernameAlreadyTakenExceptionHandler(){

    }


    @PostMapping(path = "/register")
    public @ResponseBody
    String registerNewUser(@RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String passwordHash, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode) throws UsernameAlreadyTakenException {
        PharmacyUser pharmacyUser = new PharmacyUser();

            checkUsernameExistence(username);
            pharmacyUser.setUsername(username);
            pharmacyUser.setEmail(email);
            pharmacyUser.setPharmacist(isPharmacist);
            pharmacyUser.setPasswordHash(passwordHash);
            pharmacyUserRepository.save(pharmacyUser);
            PharmFinderAddress userAddress = new PharmFinderAddress(pharmacyUser,addressStreet,addressHouseNumber,addressPostcode);
            pharmFinderAddressRepository.save(userAddress);
            pharmacyUser.setUserAddress(userAddress);
            pharmacyUserRepository.save(pharmacyUser);

            return "Saved";

    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<PharmacyUser> getAllPharmacyUsers() {
        // This returns a JSON or XML with the users
        return pharmacyUserRepository.findAll();
    }


    private void checkUsernameExistence(String username) throws UsernameAlreadyTakenException {
        Iterable<PharmacyUser> users = pharmacyUserRepository.findAll();
        for (PharmacyUser u : users) {
            if (u.getUsername().equals(username))
                throw new UsernameAlreadyTakenException("Username was already taken by someone, please try another.");
        }
    }
}
