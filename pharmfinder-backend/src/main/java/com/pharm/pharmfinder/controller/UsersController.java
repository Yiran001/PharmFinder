package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.exceptions.NoSuchUsernameException;
import com.pharm.pharmfinder.controller.exceptions.UsernameAlreadyTakenException;
import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.Role;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.OnRegistrationCompleteEvent;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

@Controller
@CrossOrigin
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
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

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
    String create(HttpServletRequest request, @RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String password, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode) throws UsernameAlreadyTakenException {
        User user = new User();

        checkUsernameExistence(username);
        user.setUsername(username);
        user.setEmail(email);
        user.setPharmacist(isPharmacist);
        user.setEnabled(false);
        user.setPasswordHash(bcryptEncoder.encode(password));
        user.setAuthorities("");
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

//            registration confirmation via email, compulsory
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));

        return "Saved";
    }

    @PutMapping(path = "/update")
    public @ResponseBody String
    update(HttpServletRequest request) throws NoSuchUsernameException {
        String originalUsername = request.getParameter("originalUsername");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        boolean isPharmacist = Boolean.parseBoolean(request.getParameter("isPharmacist"));
        String passwordHash = request.getParameter("passwordHash");
        String addressStreet = request.getParameter("addressStreet");
        String addressHouseNumber = request.getParameter("addressHouseNumber");
        String addressPostcode = request.getParameter("addressPostcode");

        checkAuthorization(request, originalUsername);

        if (!Objects.equals(originalUsername, username))
            checkUsernameExistence(username);

        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();

        for (User certainUser : users) {
            if (certainUser.getUsername().equals(originalUsername)) {
                certainUser.setUsername(username);
                certainUser.setEmail(email);
                certainUser.setPharmacist(isPharmacist);
                certainUser.setEnabled(true);
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
    String delete(HttpServletRequest request) throws NoSuchUsernameException {
        String username = request.getParameter("username");
        checkAuthorization(request, username);

        User user = new User();
        user.setUsername(username);
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        User certainUser = new User();
        for (User p : users) {
            if (p.getUsername().equals(username))
                certainUser = p;
            userRepository.deleteById(certainUser.getUserID());
            return "Deleted";
        }
        throw new NoSuchUsernameException();
    }

    @GetMapping(path = "/index")
    public @ResponseBody String index(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthorities().contains("USER_ADMIN"))
            return userRepository.findAll().toString();
        return manipulatingUser.toString();
    }

    @PutMapping(path = "/ban")
    public @ResponseBody
    String ban(HttpServletRequest request) {
        String username = request.getParameter("username");
        User user = userRepository.findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
        return "Banned";
    }

    @PutMapping(path = "/unban")
    public @ResponseBody
    String unban(HttpServletRequest request) {
        String username = request.getParameter("username");
        User user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
        return "Unbanned";
    }

    @GetMapping(path = "/registrationConfirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map<String, String> confirmRegistration(HttpServletRequest request) {

        String token = request.getParameter("token");
        VerificationToken verificationToken = jwtUserDetailsService.getVerificationToken(token);
        if (verificationToken == null) {
            return Collections.singletonMap("response", "Invalid token");
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return Collections.singletonMap("response", "message expired");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return Collections.singletonMap("response", "user enabled");
    }

    private void checkUsernameExistence(String username) throws UsernameAlreadyTakenException {
        Iterable<User> users = userRepository.findAll();
        for (User u : users) {
            if (u.getUsername().equals(username))
                throw new UsernameAlreadyTakenException("Username was already taken by someone, please try another.");
        }
    }

    private void checkAuthorization(HttpServletRequest request, String username){
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthorities().contains("USER_ADMIN"))
            return;
        if (!username.equals(jwtUsername))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong username");
    }
}
