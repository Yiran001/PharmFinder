package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.exceptions.NoSuchUsernameException;
import com.pharm.pharmfinder.controller.exceptions.UsernameAlreadyTakenException;
import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.Role;
import com.pharm.pharmfinder.model.mail.password.OnPasswordResetEvent;
import com.pharm.pharmfinder.model.mail.password.PasswordResetToken;
import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.registration.OnRegistrationCompleteEvent;
import com.pharm.pharmfinder.model.mail.registration.RegistrationVerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    String create(HttpServletRequest request, @RequestParam String username, @RequestParam String email, @RequestParam boolean isPharmacist, @RequestParam String password, @RequestParam String addressStreet, @RequestParam String addressHouseNumber, @RequestParam String addressPostcode,@RequestParam String latitude,@RequestParam String longitude) throws UsernameAlreadyTakenException {
        String registration_env_var = System.getenv("REGISTRATION_EMAIL");
        boolean no_email_registration = registration_env_var == null || !registration_env_var.equalsIgnoreCase("true");

        User user = new User();
        checkUsernameExistence(username);
        user.setUsername(username);
        user.setEmail(email);
        user.setPharmacist(isPharmacist);
//        if env var is set, and equals to true, account has to be activated via email
        user.setEnabled(no_email_registration);
        user.setPasswordHash(bcryptEncoder.encode(password));
        user.setAuthority(Role.USER);
        userRepository.save(user);
        Address userAddress = new Address(user, addressStreet, addressHouseNumber, addressPostcode);
        addressRepository.save(userAddress);
        user.setUserAddress(userAddress);
        userRepository.save(user);
        if (isPharmacist) {
            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setPharmacyAddress(userAddress);
            pharmacy.setPharmacyName(username);
            pharmacy.setUser(user);
            pharmacy.setLat(latitude);
            pharmacy.setLng(longitude);
            pharmacyRepository.save(pharmacy);
        }

//            registration confirmation via email, compulsory when activated
        if (!no_email_registration) {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        }
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
        jwtUserDetailsService.deleteUserByUsername(username);
        return "Deleted";
    }

    @GetMapping(path = "/index")
    public @ResponseBody
    Iterable<User> index(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthority() == Role.USER_ADMIN)
            return userRepository.findAll();
        ArrayList<User> list = new ArrayList<>();
        list.add(manipulatingUser);
        return list;
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
    public ResponseEntity<Map<String, String>> registrationConfirm(HttpServletRequest request) {
        String token = request.getParameter("token");
        RegistrationVerificationToken registrationVerificationToken = jwtUserDetailsService.getVerificationToken(token);
        if (registrationVerificationToken == null) {
            Map<String, String> jsonResponse = Collections.singletonMap("response", "Invalid token");
            return new ResponseEntity<>(jsonResponse, HttpStatus.FORBIDDEN);
        }

        User user = registrationVerificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((registrationVerificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            Map<String, String> jsonResponse = Collections.singletonMap("response", "message expired");
            return new ResponseEntity<>(jsonResponse, HttpStatus.FORBIDDEN);
        }

        user.setEnabled(true);
        userRepository.save(user);
        jwtUserDetailsService.deleteVerificationToken(registrationVerificationToken);

        Map<String, String> jsonResponse = Collections.singletonMap("response", "user enabled");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/passwordReset")
    public @ResponseBody String passwordReset(HttpServletRequest request){
        String username = request.getParameter("username");
        String newPassword = request.getParameter("newPassword");
        User user = userRepository.findByUsername(username);
        if (user == null)
            return "Unknown username";

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnPasswordResetEvent(appUrl, request.getLocale(), user, newPassword));
        return "Reset initialized";
    }

    @GetMapping(path = "/passwordResetConfirm")
    public ResponseEntity<Map<String, String>> passwordResetConfirm(HttpServletRequest request) {
        String token = request.getParameter("token");
        PasswordResetToken passwordResetToken = jwtUserDetailsService.getPasswordResetToken(token);
        if (passwordResetToken == null) {
            Map<String, String> jsonResponse = Collections.singletonMap("response", "invalid token");
            return new ResponseEntity<>(jsonResponse, HttpStatus.FORBIDDEN);
        }

        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            Map<String, String> jsonResponse = Collections.singletonMap("response", "token expired");
            return new ResponseEntity<>(jsonResponse, HttpStatus.FORBIDDEN);
        }

        user.setPasswordHash(bcryptEncoder.encode(passwordResetToken.getNewPassword()));
        userRepository.save(user);
        Map<String, String> jsonResponse = Collections.singletonMap("response", "password changed");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    private void checkUsernameExistence(String username) throws UsernameAlreadyTakenException {
        Iterable<User> users = userRepository.findAll();
        for (User u : users) {
            if (u.getUsername().equals(username))
                throw new UsernameAlreadyTakenException("Username was already taken by someone, please try another.");
        }
    }

    private void checkAuthorization(HttpServletRequest request, String username) {
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthority() == Role.USER_ADMIN)
            return;
        if (!username.equals(jwtUsername))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong username");
    }
}
