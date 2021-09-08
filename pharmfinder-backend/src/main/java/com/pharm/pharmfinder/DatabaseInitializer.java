package com.pharm.pharmfinder;

import com.pharm.pharmfinder.config.AdminInitializer;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// test comment
@Component
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    AdminInitializer initializer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        initializer.initialize();
    }
}
