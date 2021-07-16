package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping(path = "/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    /**
     * handles exception, that occurs when someone wants to take the same user that someone else has already took
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username was already taken")
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public void usernameAlreadyTakenExceptionHandler() {
    }

    /**
     * handles exception, that occurs when someone tries to access a non existent username
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "No such username")
    @ExceptionHandler(NoSuchUsernameException.class)
    public void noSuchUsernameExceptionHandler() {
    }


    /**
     * Method, that creates a new user entry.
     *
     * @param username
     * @param email
     * @param isPharmacist
     * @param password
     * @param addressStreet
     * @param addressHouseNumber
     * @param addressPostcode
     * @return
     * @throws UsernameAlreadyTakenException
     */
    @PostMapping(path = "/create")
    public @ResponseBody
    String create(@RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String password, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode) throws UsernameAlreadyTakenException {
//        User user = new User();
//
//        checkUsernameExistence(username);
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPharmacist(isPharmacist);
//        user.setPasswordHash(password);
//        userRepository.save(user);
//        Address userAddress = new Address(user, addressStreet, addressHouseNumber, addressPostcode);
//        addressRepository.save(userAddress);
//        user.setUserAddress(userAddress);
//        userRepository.save(user);
//
//        return "Saved";
            User user = new User();

            checkUsernameExistence(username);
            user.setUsername(username);
            user.setEmail(email);
            user.setPharmacist(isPharmacist);
            user.setPasswordHash(bcryptEncoder.encode(password));
            userRepository.save(user);
            Address userAddress = new Address(user,addressStreet,addressHouseNumber,addressPostcode);
            addressRepository.save(userAddress);
            user.setUserAddress(userAddress);
            userRepository.save(user);
            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setPharmacyAddress(userAddress);
            pharmacy.setPharmacyName(username);
            pharmacy.setOwner(user);
            pharmacyRepository.save(pharmacy);
            return "Saved";
    }

    @PutMapping(path = "/update")
    public @ResponseBody
    String update(@RequestParam String originalUsername, @RequestParam String originalPassword, @RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String passwordHash, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode) throws UsernameAlreadyTakenException, NoSuchUsernameException {

        if (originalUsername != username)
            checkUsernameExistence(username);

        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();

        for (User certainUser : users) {
            if (certainUser.getUsername().equals(originalUsername) && certainUser.getPasswordHash().equals(originalPassword)) {
                certainUser.setUsername(username);
                certainUser.setEmail(email);
                certainUser.setPharmacist(isPharmacist);
                certainUser.setPasswordHash(passwordHash);
                userRepository.save(certainUser);
                Address userAddress = new Address(certainUser, addressStreet, addressHouseNumber, addressPostcode);
                addressRepository.save(userAddress);
                certainUser.setUserAddress(userAddress);
                userRepository.save(certainUser);
                return "Updated";
            }
        }

        throw new NoSuchUsernameException();

    }

    @DeleteMapping(path = "/delete")
    public @ResponseBody
    String delete(@RequestParam String username, @RequestParam String password) throws NoSuchUsernameException {
        User user = new User();
        user.setUsername(username);
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        User certainUser = new User();
        for (User p : users) {
            if (p.getUsername().equals(username) && p.getPasswordHash().equals(password))
                certainUser = p;
            userRepository.deleteById(certainUser.getUserID());
            return "Deleted";
        }
        throw new NoSuchUsernameException();
    }

    @GetMapping(path = "/index")
    public @ResponseBody
    Iterable<User> index() {
        return userRepository.findAll();
    }


    private void checkUsernameExistence(String username) throws UsernameAlreadyTakenException {
        Iterable<User> users = userRepository.findAll();
        for (User u : users) {
            if (u.getUsername().equals(username))
                throw new UsernameAlreadyTakenException("Username was already taken by someone, please try another.");
        }
    }
}
