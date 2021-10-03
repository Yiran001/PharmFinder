package com.pharm.pharmfinder.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.jwt.jwt_model.JwtRequest;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    //    Medicine 1
    public String pzn1 = "123";
    public String friendlyName = "Aspirin";
    public MedicineForm medicineForm = MedicineForm.PILL;

    //    User 1
    String basicUsername = "name";
    String email1 = "email4@mail.com";
    String password1 = "password";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";

    String adminUsername = "Ada_Lovelace";
    String adminPassword = System.getenv("USERADMINPW");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_add_user() throws Exception {
        String username = basicUsername + "1";
        addUserViaPostRequest(username);
    }

    @Test
    void should_authenticate() throws Exception {
        String username = basicUsername + "2";
        addUserViaPostRequest(username);
        authenticate(username, password1);
    }

    @Test
    void should_add_medicine() throws Exception {
        String username = basicUsername + "3";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine1(username, jwt);
    }

    @Test
    void should_not_add_medicine_to_different_user_account() throws Exception {
        String username = basicUsername + "4";
        String username2 = basicUsername + "5";
        addUserViaPostRequest(username);
        String jwtUsername1 = authenticate(username, password1);
        addMedicine1(username, jwtUsername1);
        addMedicine1Conflict(username2, jwtUsername1);
    }

    @Test
    void should_authenticate_admin() throws Exception {
        authenticate(adminUsername, adminPassword);
    }

    @Test
    void should_ban_user() throws Exception {
        String username = basicUsername + "6";
        addUserViaPostRequest(username);
        banUser(username);
    }

    @Test
    void should_unban_user() throws Exception {
        String username = basicUsername + "7";
        addUserViaPostRequest(username);
        unbanUser(username);
    }

    @Test
    void should_not_authenticate_banned_user() throws Exception {
        String username = basicUsername + "8";
        addUserViaPostRequest(username);
        banUser(username);
        authenticateFail(username, password1);
    }

    @Test
    void should_not_let_banned_user_use_existing_jwt() throws Exception {
        String username = basicUsername + "9";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        banUser(username);
        addMedicine1Unauthorized(username, jwt);
    }

    private String authenticate(String username, String password) throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
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

    private void authenticateFail(String username, String password) throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
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

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    private void addMedicine1(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine1Unauthorized(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    private void addMedicine1Conflict(String username, String jwt) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0)
                .header("Authorization", generateAuthHeader(jwt));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isConflict());
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

    private String generateAuthHeader(String jwt){
        return "Bearer " + jwt;
    }

    private void banUser(String username) throws Exception {
        String jwtAdmin = authenticate(adminUsername, adminPassword);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/ban")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", generateAuthHeader(jwtAdmin));
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("Banned")));
    }

    private void unbanUser(String username) throws Exception {
        String jwtAdmin = authenticate(adminUsername, adminPassword);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/users/unban")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwtAdmin);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("Unbanned")));
    }
}
