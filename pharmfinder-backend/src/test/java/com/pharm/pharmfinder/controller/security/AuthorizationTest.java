package com.pharm.pharmfinder.controller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.config.AdminInitializer;
import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private AdminInitializer adminInitializer;

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

    //    user admin
    String userAdminPassword;
    String userAdminName = "Ada_Lovelace";
    //    medicine admin
    String medicineAdminPassword;
    String medicineAdminName = "Dennis_Ritchie";

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
    void user1_should_not_create_medicine_for_user2() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        addMedicine1Conflict(username2, jwtUser1);
    }

    @Test
    void user1_should_not_update_medicine_for_user2() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        updateMedicine1Conflict(username2, jwtUser1);
    }

    @Test
    void user1_should_not_delete_medicine_for_user2() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        deleteMedicine1Conflict(username2, jwtUser1);
    }

    @Test
    void user1_should_not_get_medicine_data_for_user2() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        String jwtUser2 = authenticate(username2, password2);
        addMedicine1(username2, jwtUser2);
        getIndexOnlyForOwnAccount(username1, jwtUser1, friendlyName);
    }

    @Test
    void admin_should_update_medicine_of_user() throws Exception {
        addAdmins();
        addUser1ViaPostRequest(username1);
        String jwtUser1 = authenticate(username1, password1);
        addMedicine1(username1, jwtUser1);
        String jwtAdmin = authenticate(medicineAdminName, medicineAdminPassword);
        updateMedicine1(username1, jwtAdmin);
    }

    @Test
    void admin_should_update_user_data() throws Exception {
        addAdmins();
        addUser1ViaPostRequest(username1);
        String jwtAdmin = authenticate(userAdminName, userAdminPassword);
        updateUser("NewUsername", jwtAdmin, username1);
    }

    @Test
    void admin_should_read_medicines_of_user() throws Exception {
        addAdmins();
        addUser1ViaPostRequest(username1);
        String jwtUser1 = authenticate(username1, password1);
        addMedicine1(username1, jwtUser1);
        addMedicine2(username1, jwtUser1);
        String jwtAdmin = authenticate(medicineAdminName, medicineAdminPassword);
        indexMedicinesAsAdmin(pzn1, pzn2, username1, jwtAdmin);
    }

    @Test
    void admin_should_read_user_data() throws Exception {
        addAdmins();
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        String jwtUser2 = authenticate(username2, password2);
        addMedicine1(username1, jwtUser1);
        addMedicine2(username2, jwtUser2);
        String jwtAdmin = authenticate(userAdminName, userAdminPassword);
        indexUsersAsAdmin(username1, username2, jwtAdmin);
    }

    @Test
    void user1_should_not_update_user2_account() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        updateUserForbidden("newUsername", jwtUser1, username2);
    }

    @Test
    void user1_should_not_delete_user2_account() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        deleteUserForbidden(username2, jwtUser1);
    }

    @Test
    void user1_should_not_read_user2_account() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        indexUserOnlyPersonalInfo(username2, jwtUser1);
    }

    @Test
    void user1_should_not_ban_user2_account() throws Exception {
        addUser1ViaPostRequest(username1);
        addUser2ViaPostRequest(username2);
        String jwtUser1 = authenticate(username1, password1);
        banUserForbidden(jwtUser1, username2);
    }

    @Test
    void userAdmin_should_ban_user_account() throws Exception {
        addAdmins();
        addUser1ViaPostRequest(username1);
        String jwtAdmin = authenticate(userAdminName, userAdminPassword);
        banUser(jwtAdmin, username1);
    }

    private void banUserForbidden(String jwt, String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserBanRequest(username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private void banUser(String jwt, String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserBanRequest(username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addUser1ViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email1, true, password1, street1, houseNumber1, postcode1);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addUser2ViaPostRequest(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPostRequest
                (username, email2, true, password2, street2, houseNumber2, postcode2);
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

    private MockHttpServletRequestBuilder buildUserDeleteRequest(String username) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("/users/delete")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildUserBanRequest(String username) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/ban")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildUserIndexRequest() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/users/index")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildMedicinesIndexRequest(String username) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/medicines/index")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildUserPutRequest
            (String originalUsername, String username, String email, boolean isPharmacist, String password, String addressStreet,
             String addressHouseNumber, String addressPostcode) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/update")
                .param("originalUsername", originalUsername)
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
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine2(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn2, friendlyName2, String.valueOf(medicineForm2), username, 2)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine1Conflict(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    private void updateMedicine1(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePutRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 10)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void updateMedicine1Conflict(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePutRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    private void updateUserForbidden(String username, String jwt, String originalUsername) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPutRequest
                (originalUsername, username, email1, true, password1, street1, houseNumber1, postcode1)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private void updateUser(String username, String jwt, String originalUsername) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserPutRequest
                (originalUsername, username, email1, true, password1, street1, houseNumber1, postcode1)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void deleteUserForbidden(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserDeleteRequest(username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private void indexUserOnlyPersonalInfo(String mustNotContain, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserIndexRequest()
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(not(containsString(mustNotContain))));

    }

    private void indexMedicinesAsAdmin(String mustContain1, String mustContain2, String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinesIndexRequest(username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString(mustContain1)))
                .andExpect(content().string(containsString(mustContain2)));
    }

    private void indexUsersAsAdmin(String mustContain1, String mustContain2, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildUserIndexRequest()
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString(mustContain1)))
                .andExpect(content().string(containsString(mustContain2)));
    }

    private void deleteMedicine1Conflict(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicineDeleteRequest
                (pzn1, username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    private void getIndexOnlyForOwnAccount(String username, String jwt, String mustNotContain) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicineGetRequest
                (username)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(not(containsString(mustNotContain))));
    }

    private MockHttpServletRequestBuilder buildMedicinePostRequest
            (String pzn, String friendlyName, String medicineForm, String username, int amount) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/medicines/create")
                .param("pzn", pzn)
                .param("friendlyName", friendlyName)
                .param("medicineForm", medicineForm)
                .param("username", username)
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildMedicinePutRequest
            (String pzn, String friendlyName, String medicineForm, String username, int amount) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/medicines/update")
                .param("pzn", pzn)
                .param("friendlyName", friendlyName)
                .param("medicineForm", medicineForm)
                .param("username", username)
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildMedicineDeleteRequest
            (String pzn, String username) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("/medicines/delete")
                .param("username", username)
                .param("pzn", pzn)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private MockHttpServletRequestBuilder buildMedicineGetRequest
            (String username) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/medicines/index")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        return builder;
    }

    private void addAdmins() {
        userAdminPassword = System.getenv("USERADMINPW");
        medicineAdminPassword = System.getenv("MEDICINEADMINPW");
        adminInitializer.initialize();
    }

}

