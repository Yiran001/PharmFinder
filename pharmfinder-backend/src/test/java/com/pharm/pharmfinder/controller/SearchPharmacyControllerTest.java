package com.pharm.pharmfinder.controller;


import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.MedicineForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class SearchPharmacyControllerTest {
    //test-user1
    private String username1 = "max";
    private String emailU1 = "max@gmail.com";
    private boolean isPU1 = true;
    private String passwordU1 = "4384754";
    private String streetU1 = "Kreuzstrasse";
    private String houseNumberU1 = "24";
    private String postcodeU1 = "16225";
    //test-user2
    private String username2 = "harry";
    private String emailU2 = "harry@web.de";
    private boolean isPU2 = false;
    private String passwordU2 = "1234567";
    private String streetU2 = "Breite Strasse";
    private String houseNumberU2 = "102";
    private String postcodeU2 = "16225";
    //medicine
    private String pznM1="12345678";
    private String friendlyName="Paracetamol";
    private MedicineForm form=MedicineForm.PILL;
    private String amount="5";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PharmacyMedicineRepository pharmacyMedicine;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserRepository userRepository;


    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    public void should_add_user() throws Exception {
        String username = username1 + "1";
        addUser1ViaPostRequest(username1);
    }

    @Test
    public void testDistancePharmacy() throws Exception {
        addUser1ViaPostRequest(username1);

    }
    private void addUser1ViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, emailU1, true, passwordU1, streetU1, houseNumberU1, postcodeU1);
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
    private String generateAuthHeader(String jwt){
        return "Bearer " + jwt;
    }

    private MockHttpServletRequestBuilder buildMedicinePostRequest
            (String pzn, String friendlyName, String medicineForm, String username, int amount) {
        return MockMvcRequestBuilders
                .post("/medicines/create")
                .param("pzn", pzn)
                .param("friendlyName", friendlyName)
                .param("medicineForm", medicineForm)
                .param("username", username)
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }


}
