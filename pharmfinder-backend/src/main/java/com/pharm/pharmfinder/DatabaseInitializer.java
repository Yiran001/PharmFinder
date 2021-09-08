package com.pharm.pharmfinder;

import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    private String password = System.getenv("ADMINPW");

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@email.com");
        admin.setPharmacist(false);
        admin.setEnabled(true);
        admin.setPasswordHash(bcryptEncoder.encode(password));
        userRepository.save(admin);
    }
}
