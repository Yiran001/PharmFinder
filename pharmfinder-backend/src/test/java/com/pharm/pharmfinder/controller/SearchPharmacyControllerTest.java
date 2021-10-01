package com.pharm.pharmfinder.controller;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.controller.repositories.*;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.MedicineForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.containsString;
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
    private String lat = "53.83543";
    private String lng="49.89322";
    //test-user2
    private String username2 = "harry";
    private String emailU2 = "harry@web.de";
    private boolean isPU2 = false;
    private String passwordU2 = "1234567";
    private String streetU2 = "Breite Strasse";
    private String houseNumberU2 = "102";
    private String postcodeU2 = "16225";
    private String lat2 = "53.53543";
    private String lng2="49.69322";
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
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private AddressRepository addressRepository;


    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @BeforeEach
    void setUp() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }
    @Test
    public void should_add_user() throws Exception {
        String username = username1 + "1";
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);

    }
    @Test
    void should_authenticate() throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        authenticate(username1, passwordU1);

    }
    @Test
    void should_add_medicine() throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        String jwt = authenticate(username1, passwordU1);
        addMedicine1(username1, jwt);
    }

    @Test
    public void registerUsersAndAddMedicine() throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        String jwtU1 = authenticate(username1, passwordU1);
        addMedicine1(username1, jwtU1);
        addUserViaPostRequest(username2,emailU2,isPU2,passwordU2,streetU2,houseNumberU2,postcodeU2,lat2,lng2);
        String jwtU2 = authenticate(username1, passwordU1);
        searchForPharmacy(pznM1,lat2,lng2,jwtU2);
    }



    private void addUserViaPostRequest(String username, String email, boolean isPharmacist, String password, String street, String housenumber, String postcode, String latitude, String longitude) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email, isPharmacist, password, street, housenumber, postcode,latitude,longitude);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    private MockHttpServletRequestBuilder buildUserPostRequest
            (String username, String email, boolean isPharmacist, String password, String addressStreet,
             String addressHouseNumber, String addressPostcode,String latitude,String longitude) {
        return MockMvcRequestBuilders
                .post("/users/create")
                .param("username", username)
                .param("email", email)
                .param("isPharmacist", String.valueOf(isPharmacist))
                .param("password", password)
                .param("addressStreet", addressStreet)
                .param("addressHouseNumber", addressHouseNumber)
                .param("addressPostcode", addressPostcode)
                .param("latitude", latitude)
                .param("longitude",longitude)
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
    private MockHttpServletRequestBuilder buildSearchPharmacyPostRequest(String pzn,String latitude,String longitude){
        return MockMvcRequestBuilders
                .get("/search/pharmacy")
                .param("pzn", pzn)
                .param("latitude", latitude)
                .param("longitude", longitude)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }
    private String authenticate(String username, String password) throws Exception {
        com.pharm.pharmfinder.jwt.jwt_model.JwtRequest jwtRequest = new com.pharm.pharmfinder.jwt.jwt_model.JwtRequest();
        jwtRequest.setUsername(username);
        jwtRequest.setPassword(password);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(jwtRequest);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/authenticate")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson);

        MvcResult result = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("token")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String[] jsonPair = response.split(":");
        String rawJwt = jsonPair[1];
        return rawJwt.substring(1, rawJwt.length()-2);
    }
    private void addMedicine1(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pznM1, friendlyName, String.valueOf(form), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    private void searchForPharmacy(String pzn,String latitude,String longitude,String jwt)throws Exception{
        MockHttpServletRequestBuilder builder = buildSearchPharmacyPostRequest
                (pzn, latitude,longitude)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
