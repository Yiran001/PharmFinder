package com.pharm.pharmfinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        String registration_env_var = System.getenv("REGISTRATION_EMAIL");
        boolean no_email_registration = registration_env_var == null || !registration_env_var.equalsIgnoreCase("true");
        if (no_email_registration){
            return new JavaMailSenderImpl();
        }

        mailSender.setUsername(System.getenv("EMAILADDRESS"));
        mailSender.setPassword(System.getenv("EMAILPW"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
