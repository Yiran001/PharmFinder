package com.pharm.pharmfinder.mail;

import com.pharm.pharmfinder.controller.repositories.PasswordTokenRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.mail.pw_reset.PasswordResetToken;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Assertions;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordReset {
//    test like in registration
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @SpyBean
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    //    User 1
    String baseUsername = "name";
    String email1 = "PharmacyFinderHTW@gmail.com";
    String password1 = "password";
    String newPassword = "mynewPw123";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";


    @Test
    void should_request_password_reset() throws Exception {
        String username = baseUsername + "0";
        addUserViaPostRequest(baseUsername);
//        String token = getPasswordResetTokenForUsername(username1);
        checkPasswordResetRequest(baseUsername, newPassword);
        jwtUserDetailsService.deleteUserByUsername(baseUsername);
    }

    @Test
    void should_confirm_password_reset() throws Exception {
        String username = baseUsername + "1";
        addUserViaPostRequest(username);
        checkPasswordResetRequest(username, newPassword);
        String token = getPasswordResetTokenForUsername(username);
        checkPasswordResetConfirmation(token);
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    @Test
    void should_send_mail() throws Exception {
        String username = baseUsername + "2";
        addUserViaPostRequest(username);
        verify(javaMailSender).send((SimpleMailMessage) any());
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    @Test
    void should_change_password() throws Exception {
        String username = baseUsername + "3";
        addUserViaPostRequest(username);
        checkPasswordResetRequest(username, newPassword);
        String token = getPasswordResetTokenForUsername(username);
        checkPasswordResetConfirmation(token);
        User userChanged = userRepository.findByUsername(username);
        Assertions.assertTrue(bcryptEncoder.matches(newPassword, userChanged.getPasswordHash()));
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    @Test
    void should_not_verify_with_expired_token() throws Exception {
        String username = baseUsername + "4";
        addUserViaPostRequest(username);
        checkPasswordResetRequest(username, newPassword);
        User user = userRepository.findByUsername(username);
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByUser(user);
        passwordResetToken.setExpiryDate(calculateExpiryDate(50));
        passwordTokenRepository.save(passwordResetToken);
        Thread.sleep(51L);
        checkPasswordResetConfirmationForbidden(passwordResetToken.getToken());
        User userChanged = userRepository.findByUsername(username);
        Assertions.assertTrue(bcryptEncoder.matches(password1, userChanged.getPasswordHash()));
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    @Test
    void should_not_verify_with_invalid_token() throws Exception {
        String username = baseUsername + "5";
        addUserViaPostRequest(username);
        checkPasswordResetRequest(username, newPassword);
        checkPasswordResetConfirmationForbidden(UUID.randomUUID().toString());
        User userChanged = userRepository.findByUsername(username);
        Assertions.assertTrue(bcryptEncoder.matches(password1, userChanged.getPasswordHash()));
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    private Date calculateExpiryDate(int expiryTimeInMs) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MILLISECOND, expiryTimeInMs);
        return new Date(cal.getTime().getTime());
    }

    private void checkPasswordResetRequest(String username, String newPassword) throws Exception {
        MockHttpServletRequestBuilder builder = buildPasswordResetRequest(username, newPassword);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void checkPasswordResetConfirmation(String token) throws Exception {
        MockHttpServletRequestBuilder builder = buildPasswordResetConfirmationRequest(token);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void checkPasswordResetConfirmationForbidden(String token) throws Exception {
        MockHttpServletRequestBuilder builder = buildPasswordResetConfirmationRequest(token);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private String getPasswordResetTokenForUsername(String username){
        User user = userRepository.findByUsername(username);
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByUser(user);
        return passwordResetToken.getToken();
    }

    private void addUserViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email1, true, password1, street1, houseNumber1, postcode1);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private MockHttpServletRequestBuilder buildUserPostRequest
            (String username, String email, boolean isPharmacist, String password, String addressStreet,
             String addressHouseNumber, String addressPostcode) {
        return MockMvcRequestBuilders
                .post("/users/create")
                .param("username", username)
                .param("email", email)
                .param("isPharmacist", String.valueOf(isPharmacist))
                .param("password", password)
                .param("addressStreet", addressStreet)
                .param("addressHouseNumber", addressHouseNumber)
                .param("addressPostcode", addressPostcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder buildPasswordResetConfirmationRequest(String token) {
        return MockMvcRequestBuilders
                .get("/users/passwordResetConfirm")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder buildPasswordResetRequest
            (String username, String newPassword) {
        return MockMvcRequestBuilders
                .post("/users/passwordReset")
                .param("username", username)
                .param("newPassword", newPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }

}
