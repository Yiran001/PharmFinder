package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.registration.RegistrationVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<RegistrationVerificationToken, Long> {

    RegistrationVerificationToken findByToken(String token);

    RegistrationVerificationToken findByUser(User user);
}
