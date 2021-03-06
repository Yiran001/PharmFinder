package com.pharm.pharmfinder.controller.repositories;

import com.pharm.pharmfinder.model.mail.password.PasswordResetToken;
import com.pharm.pharmfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

}
