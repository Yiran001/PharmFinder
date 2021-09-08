package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
