package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderUserRepository;
import com.pharm.pharmfinder.model.PharmFinderAddress;
import com.pharm.pharmfinder.model.PharmFinderUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping(path = "/user")
public class PharmFinderUserController {

    @Autowired
    private PharmFinderUserRepository pharmFinderUserRepository;
    @Autowired
    private PharmFinderAddressRepository pharmFinderAddressRepository;

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username was already taken")
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public void usernameAlreadyTakenExceptionHandler(){

    }


    @PostMapping(path = "/register")
    public @ResponseBody
    String registerNewUser(@RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String passwordHash, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode) throws UsernameAlreadyTakenException {
        PharmFinderUser pharmFinderUser = new PharmFinderUser();

            checkUsernameExistence(username);
            pharmFinderUser.setUsername(username);
            pharmFinderUser.setEmail(email);
            pharmFinderUser.setPharmacist(isPharmacist);
            pharmFinderUser.setPasswordHash(passwordHash);
            pharmFinderUserRepository.save(pharmFinderUser);
            PharmFinderAddress userAddress = new PharmFinderAddress(pharmFinderUser,addressStreet,addressHouseNumber,addressPostcode);
            pharmFinderAddressRepository.save(userAddress);
            pharmFinderUser.setUserAddress(userAddress);
            pharmFinderUserRepository.save(pharmFinderUser);

            return "Saved";

    }
    @PostMapping(path = "/delete")
    public @ResponseBody
    String registerNewUser(@RequestParam String username, @RequestParam String password) throws UsernameAlreadyTakenException {
        PharmFinderUser pharmFinderUser = new PharmFinderUser();

        pharmFinderUser.setUsername(username);
        ArrayList<PharmFinderUser> pharmFinderUsers = (ArrayList<PharmFinderUser>) pharmFinderUserRepository.findAll();
        PharmFinderUser certainUser = new PharmFinderUser();
        for(PharmFinderUser p : pharmFinderUsers){
            if(p.getUsername().equals(username) && p.getPasswordHash().equals(password))
                certainUser = p;
        }
        pharmFinderUserRepository.deleteById(certainUser.getUserID());
        return "Deleted";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<PharmFinderUser> getAllPharmacyUsers() {
        // This returns a JSON or XML with the users
        return pharmFinderUserRepository.findAll();
    }


    private void checkUsernameExistence(String username) throws UsernameAlreadyTakenException {
        Iterable<PharmFinderUser> users = pharmFinderUserRepository.findAll();
        for (PharmFinderUser u : users) {
            if (u.getUsername().equals(username))
                throw new UsernameAlreadyTakenException("Username was already taken by someone, please try another.");
        }
    }
}
