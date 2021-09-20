package com.pharm.pharmfinder.mail.pw_reset;

import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    private JwtUserDetailsService service;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent onPasswordResetEvent) {
        this.confirmPasswordReset(onPasswordResetEvent);
    }

    private void confirmPasswordReset(OnPasswordResetEvent event){
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        String newPassword = event.getNewPassword();
        service.createPasswordResetTokenForUser(user, token, newPassword);

        String recipientAddress = user.getEmail();
        String subject = "Password reset Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/users/passwordResetConfirm?token=" + token;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
//        todo vor deployment mit heroku domain ersetzen
        email.setText("http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}
