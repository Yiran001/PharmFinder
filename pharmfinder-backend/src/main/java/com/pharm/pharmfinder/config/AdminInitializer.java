package com.pharm.pharmfinder.config;

import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
//todo: vor deployment env vars auf heroku anpassen

    private String userAdminPw = System.getenv("USERADMINPW");

    private String medicineAdminPw = System.getenv("MEDICINEADMINPW");

    private final List<String> passwords = List.of(
            userAdminPw,
            medicineAdminPw
    );

    private final List<String> fullNames = List.of(
            "Ada_Lovelace",
            "Dennis_Ritchie"
    );
    private final List<String> emailAddresses = List.of(
            "ada.lovelace@nix.io",
            "dennis.ritchie@nix.io"
    );
    private final List<String> roles = List.of(
            "USER_ADMIN",
            "MEDICINE_ADMIN"
    );

    public void initialize() {
        for (int i = 0; i < fullNames.size(); i++) {
            String username = fullNames.get(i);
//            if (userRepository.findByUsername(username) == null){
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(emailAddresses.get(i));
            admin.setPharmacist(false);
            admin.setEnabled(true);
            admin.setPasswordHash(bcryptEncoder.encode(passwords.get(i)));
            admin.setAuthorities(roles.get(i));
            userRepository.save(admin);
//            }
        }
    }
}
