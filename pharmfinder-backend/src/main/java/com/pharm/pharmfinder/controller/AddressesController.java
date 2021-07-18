package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.exceptions.NoSuchAddressException;
import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping(path = "/addresses")
public class AddressesController {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * handles exception, that occurs when someone tries to access an address which is non existent
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "There is no such address")
    @ExceptionHandler(NoSuchAddressException.class)
    public void noSuchAddressExceptionHandler() {
    }

    @PostMapping(path = "/create")
    public @ResponseBody
    String create(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode) {
        Address address = new Address();

        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostcode(postcode);

        addressRepository.save(address);

        return "Saved";
    }

    @DeleteMapping(path = "/delete")
    public @ResponseBody
    String delete(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode) throws NoSuchAddressException {
        ArrayList<Address> addresses = (ArrayList<Address>) addressRepository.findAll();

        for (Address p : addresses) {
            if (p.getStreet().equals(street) && p.getHouseNumber().equals(houseNumber) && p.getPostcode().equals(postcode)) {
                addressRepository.deleteById(p.getAddressID());
                return "Deleted";
            }
        }
        throw new NoSuchAddressException();
    }

    @PutMapping(path = "/update")
    public @ResponseBody
    String update(@RequestParam String street, @RequestParam String houseNumber, @RequestParam String postcode, @RequestParam String newStreet, @RequestParam String newHouseNumber, @RequestParam String newPostcode) throws NoSuchAddressException {
        ArrayList<Address> addresses = (ArrayList<Address>) addressRepository.findAll();

        for (Address p : addresses) {
            if (p.getStreet().equals(street) && p.getHouseNumber().equals(houseNumber) && p.getPostcode().equals(postcode)) {
                p.setStreet(newStreet);
                p.setHouseNumber(newHouseNumber);
                p.setPostcode(newPostcode);
                addressRepository.save(p);
                return "Updated";
            }
        }
        throw new NoSuchAddressException();
    }

    @GetMapping(path = "/index")
    public @ResponseBody
    Iterable<Address> index() {
        return addressRepository.findAll();
    }


}
