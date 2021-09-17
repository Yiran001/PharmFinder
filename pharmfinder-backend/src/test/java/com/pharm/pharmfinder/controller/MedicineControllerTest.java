package com.pharm.pharmfinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.*;
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
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class MedicineControllerTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    //    Medicine 1
    public String pzn1 = "123";
    public String friendlyName = "Aspirin";
    public MedicineForm medicineForm = MedicineForm.PILL;

    //    Medicine 2
    public String pzn2 = "124";
    public String friendlyName2 = "Tylenol";
    public MedicineForm medicineForm2 = MedicineForm.PILL;

    //    User 1
    String username1 = "name";
    String email1 = "email4@mail.com";
    String password1 = "password";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";
    //    User 2
    String username2 = "name2";
    String email2 = "email5@mail.com";
    String password2 = "password2";
    String street2 = "streetTwo";
    String houseNumber2 = "2";
    String postcode2 = "23456";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Test
    void should_save_multiple_medicines_in_one_pharmacy() throws Exception {
        addPharmacistUser1();
        String jwt = authenticate(username1, password1);
        addMedicine1(username1, jwt);
        addMedicine2(username1, jwt);
        getAndVerifyResponse(username1, "pzn='" + pzn1 + "'", jwt);
        getAndVerifyResponse(username1, "pzn='124'", jwt);
        jwtUserDetailsService.deleteUserByUsername(username1);
    }

    @Test
    void should_not_return_medicines_from_different_user() throws Exception {
        addPharmacistUser1();
        addPharmacistUser2();
        String jwt1 = authenticate(username1, password1);
        String jwt2 = authenticate(username2, password2);
        addMedicine1(username1, jwt1);
        getAndVerifyResponse(username2, "No medicines registered", jwt2);
        jwtUserDetailsService.deleteUserByUsername(username1);
        jwtUserDetailsService.deleteUserByUsername(username2);

    }

    @Test
    void should_not_return_medicine_after_deleting() throws Exception {
        addPharmacistUser1();
        String jwt1 = authenticate(username1, password1);
        addMedicine1(username1, jwt1);
        deregisterMedicine(pzn1, username1, jwt1);
        getAndVerifyResponse(username1, "No medicines registered", jwt1);
        jwtUserDetailsService.deleteUserByUsername(username1);
    }

    @Test
    void should_change_amount_on_update() throws Exception {
        int newAmount = 15;
        addPharmacistUser1();
        String jwt1 = authenticate(username1, password1);
        addMedicine1(username1, jwt1);
        updateMedicine(pzn1, friendlyName, medicineForm, username1, newAmount, jwt1);
        getAndVerifyResponse(username1, "Amount: " + newAmount, jwt1);
        jwtUserDetailsService.deleteUserByUsername(username1);
    }

    @Test
    void should_change_friendlyName_on_update() throws Exception {
        String newFriendlyName = "newFriendlyName";
        addPharmacistUser1();
        String jwt1 = authenticate(username1, password1);
        addMedicine1(username1, jwt1);
        updateMedicine(pzn1, newFriendlyName, medicineForm, username1, 0, jwt1);
        getAndVerifyResponse(username1, "friendlyName='" + newFriendlyName + "'", jwt1);
        jwtUserDetailsService.deleteUserByUsername(username1);
    }

    @Test
    void should_save_one_medicine_to_multiple_pharmacys() throws Exception {
        addPharmacistUser1();
        addPharmacistUser2();
        String jwt1 = authenticate(username1, password1);
        String jwt2 = authenticate(username2, password2);
        addMedicine1(username1, jwt1);
        addMedicine1(username2, jwt2);
        getAndVerifyResponse(username1, "pzn='" + pzn1 + "'", jwt1);
        getAndVerifyResponse(username2, "pzn='" + pzn1 + "'", jwt2);
        jwtUserDetailsService.deleteUserByUsername(username1);
        jwtUserDetailsService.deleteUserByUsername(username2);
    }

    private void addMedicine1(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine2(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn2, friendlyName2, String.valueOf(medicineForm2), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private String generateAuthHeader(String jwt){
        return "Bearer " + jwt;
    }

    private void addUserViaPostRequest(String username, String email, boolean isPharmacist, String password, String addressStreet,
                                       String addressHouseNumber, String addressPostcode) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email, isPharmacist, password, addressStreet, addressHouseNumber, addressPostcode);
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


    private void addPharmacistUser1() throws Exception {
        addUserViaPostRequest(username1, email1, true, password1, street1,houseNumber1, postcode1);

    }

    private void addPharmacistUser2() throws Exception {
        addUserViaPostRequest(username2, email2, true, password2, street2, houseNumber2, postcode2);
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

    private void updateMedicine(String pzn, String friendlyName, MedicineForm medicineForm, String username, int amount, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePutRequest
                (pzn, friendlyName, String.valueOf(medicineForm), username, amount)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
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

    private MockHttpServletRequestBuilder buildMedicinePutRequest
            (String pzn, String friendlyName, String medicineForm, String username, int amount) {
        return MockMvcRequestBuilders
                .put("/medicines/update")
                .param("pzn", pzn)
                .param("friendlyName", friendlyName)
                .param("medicineForm", medicineForm)
                .param("username", username)
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private void deregisterMedicine(String pzn, String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("/medicines/delete")
                .param("pzn", pzn)
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void getAndVerifyResponse(String username, String expectedOutput, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/medicines/index")
                .param("username", username)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString(expectedOutput)));
    }

    private void savePharmacyMedicineToMedicine(Medicine medicine, PharmacyMedicine pharmacyMedicine) {
        Set<PharmacyMedicine> pharmacyMedicines = medicine.getPharmacyMedicines();
        pharmacyMedicines.add(pharmacyMedicine);
        medicine.setPharmacyMedicines(pharmacyMedicines);
    }

    private void savePharmacyMedicineToEmptyPharmacy(Pharmacy pharmacy, PharmacyMedicine pharmacyMedicine) {
        Set<PharmacyMedicine> pharmacyMedicines = new HashSet<>();
        pharmacyMedicines.add(pharmacyMedicine);
        pharmacy.setPharmacyMedicines(pharmacyMedicines);
    }

    private Pharmacy getPharmacy(String name) {
        Pharmacy pharmacy = null;
        for (Pharmacy potentialPharmacy : pharmacyRepository.findAll()) {
            if (potentialPharmacy.getPharmacyName().equals(name)) {
                pharmacy = potentialPharmacy;
            }
        }
        if (pharmacy == null)
            throw new IllegalStateException("Pharmacy not found");
        return pharmacy;
    }
}
