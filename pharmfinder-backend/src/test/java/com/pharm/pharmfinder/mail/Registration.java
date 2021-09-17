package com.pharm.pharmfinder.mail;

import com.pharm.pharmfinder.controller.repositories.*;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.*;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Assertions;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class Registration {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @SpyBean
    private JavaMailSender javaMailSender;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    //    User data
    String baseUsername  = "dessen";
    String email = "PharmacyFinderHTW@gmail.com";
    String password = "password";
    String street = "streetOne";
    String houseNumber = "1";
    String postcode = "12345";

    @Test
    void should_enable_user() throws Exception {
        String username = baseUsername + "1";
        addUserViaPostRequest(username);
        assertUserIsDisabled(username);
        checkRegistrationConfirmation(getRegistrationTokenForUsername(username));
        assertUserIsEnabled(username);
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
    void should_not_verify_with_expired_token() throws Exception {
        String username = baseUsername + "3";
        addUserViaPostRequest(username);
        assertUserIsDisabled(username);
        User user = userRepository.findByUsername(username);
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        verificationToken.setExpiryDate(calculateExpiryDate(50));
        verificationTokenRepository.save(verificationToken);
        Thread.sleep(51L);
        checkRegistrationConfirmationForbidden(getRegistrationTokenForUsername(username));
        assertUserIsDisabled(username);
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    @Test
    void should_not_verify_with_invalid_token() throws Exception {
        String username = baseUsername + "4";
        addUserViaPostRequest(username);
        assertUserIsDisabled(username);
        checkRegistrationConfirmationForbidden(UUID.randomUUID().toString());
        assertUserIsDisabled(username);
        jwtUserDetailsService.deleteUserByUsername(username);
    }

    private String getRegistrationTokenForUsername(String username){
        User user = userRepository.findByUsername(username);
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        return verificationToken.getToken();
    }

    private void checkRegistrationConfirmation(String token) throws Exception {
        MockHttpServletRequestBuilder builder = buildEmailRegistrationRequest
                (token);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void checkRegistrationConfirmationForbidden(String token) throws Exception {
        MockHttpServletRequestBuilder builder = buildEmailRegistrationRequest
                (token);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private void addUserViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email, true, password, street, houseNumber, postcode);
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

    private MockHttpServletRequestBuilder buildEmailRegistrationRequest
            (String token) {
        return MockMvcRequestBuilders
                .get("/users/registrationConfirm")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private Date calculateExpiryDate(int expiryTimeInMs) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MILLISECOND, expiryTimeInMs);
        return new Date(cal.getTime().getTime());
    }

    private void assertUserIsDisabled(String username){
        User user = userRepository.findByUsername(username);
        Assertions.assertFalse(user.isEnabled());
    }

    private void assertUserIsEnabled(String username){
        User user = userRepository.findByUsername(username);
        Assertions.assertTrue(user.isEnabled());
    }
}

