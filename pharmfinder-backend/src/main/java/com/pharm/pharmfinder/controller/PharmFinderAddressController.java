package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.model.PharmFinderAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping(path = "/address")
public class PharmFinderAddressController {

    @Autowired
    private PharmFinderAddressRepository pharmFinderAddressRepository;

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "There is no such address")
    @ExceptionHandler(NoSuchAddressException.class)
    public void noSuchAddressExceptionHandler() {
    }

    @PutMapping(path = "/add")
    public @ResponseBody
    String addAddress(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode) {
        PharmFinderAddress address = new PharmFinderAddress();

        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostcode(postcode);

        pharmFinderAddressRepository.save(address);

        return "Saved";
    }

    @DeleteMapping(path = "/delete")
    public @ResponseBody
    String deleteAddress(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode) throws NoSuchAddressException {
        ArrayList<PharmFinderAddress> pharmFinderAddresses = (ArrayList<PharmFinderAddress>) pharmFinderAddressRepository.findAll();

        for (PharmFinderAddress p : pharmFinderAddresses) {
            if (p.getStreet().equals(street) && p.getHouseNumber().equals(houseNumber) && p.getPostcode().equals(postcode)) {
                pharmFinderAddressRepository.deleteById(p.getAddressID());
                return "Deleted";
            }
        }
        throw new NoSuchAddressException();
    }

    @PostMapping(path = "/update")
    public @ResponseBody
    String updateAddress(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode, @RequestParam String newStreet, @RequestParam String newHouseNumber, @RequestParam String newPostcode) throws NoSuchAddressException {
        ArrayList<PharmFinderAddress> pharmFinderAddresses = (ArrayList<PharmFinderAddress>) pharmFinderAddressRepository.findAll();

        for (PharmFinderAddress p : pharmFinderAddresses) {
            if (p.getStreet().equals(street) && p.getHouseNumber().equals(houseNumber) && p.getPostcode().equals(postcode)) {
                p.setStreet(newStreet);
                p.setHouseNumber(newHouseNumber);
                p.setPostcode(newPostcode);
                pharmFinderAddressRepository.save(p);
                return "Updated";
            }
        }
        throw new NoSuchAddressException();
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<PharmFinderAddress> getAllPharmFinderAddresses() {
        return pharmFinderAddressRepository.findAll();
    }


}
