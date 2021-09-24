package com.pharm.pharmfinder.search_and_filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class SearchAndFilterTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    @Autowired
    private MockMvc mockMvc;


    //    User 1
    String baseUsername = "test_name";
    String email1 = "PharmacyFinderHTW@gmail.com";
    String password1 = "password";
    String newPassword = "mynewPw123";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";

    @Test
    void should_search_for_pzn() throws Exception {
        String username = baseUsername + "0";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
    }

    private void searchForPznRequest(String username) throws Exception {
//        MockHttpServletRequestBuilder builder = buildUserPostRequest
//                (username, email1, true, password1, street1, houseNumber1, postcode1);
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
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
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
}
