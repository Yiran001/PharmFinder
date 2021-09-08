package com.pharm.pharmfinder.mail;

import com.pharm.pharmfinder.controller.repositories.*;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class Registration {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    //    User 1
    String username1 = "name";
    String email1 = "PharmacyFinderHTW@gmail.com";
    String password1 = "password";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";

    //    other repos are needed for proper deletion
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private MedicineRepository medicineRepository;

//    @BeforeEach
//    void setUp() {
//        pharmacyRepository.deleteAll();
//        addressRepository.deleteAll();
//        userRepository.deleteAll();
//        verificationTokenRepository.deleteAll();
//        medicineRepository.deleteAll();
//    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        verificationTokenRepository.deleteAll();
        medicineRepository.deleteAll();
    }

    @Test
    void should_enable_user() throws Exception {
        addUser1ViaPostRequest(username1);
        checkRegistrationConfirmation(getRegistrationTokenForUsername(username1));
    }

//    @Test
//    void should_send_mail() throws Exception {
//        JavaMailSender javaMailSender = mock(JavaMailSender.class);
//        RegistrationListener listener = new RegistrationListener(javaMailSender);
//        addUser1ViaPostRequest(username1);
//
////        SimpleMailMessage email = new SimpleMailMessage();
////        email.setTo(email1);
////        email.setSubject("Registration Confirmation");
////        String token = getTokenForUsername(username1);
////        email.setText("http://localhost:8080" + "/registrationConfirm?token=" + token);
//
//        confirmRegistration(getTokenForUsername(username1));
//        verify(javaMailSender).send((SimpleMailMessage) anyObject());
//    }


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

    private void confirmRegistration(String token) throws Exception {
        MockHttpServletRequestBuilder builder = buildEmailRegistrationRequest
                (token);
        this.mockMvc.perform(builder);
    }

    private void addUser1ViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email1, true, password1, street1, houseNumber1, postcode1);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private MockHttpServletRequestBuilder buildUserPostRequest
            (String username, String email, boolean isPharmacist, String password, String addressStreet,
             String addressHouseNumber, String addressPostcode) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
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
        return builder;
    }

    private MockHttpServletRequestBuilder buildEmailRegistrationRequest
            (String token) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/users/registrationConfirm")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }
}

