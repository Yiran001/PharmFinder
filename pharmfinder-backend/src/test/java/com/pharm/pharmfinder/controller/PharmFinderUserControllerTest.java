package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderPharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderUserRepository;
import org.apache.tomcat.jni.Address;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
class PharmFinderUserControllerTest {

    //test-user1
    private String tu1Username = "marry25";
    private String tu1Email = "marryberry@exampssleproivider.com";
    private boolean tu1IsPharmacist = true;
    private String tu1PasswordHash = "sdfaijufgahjipufewiuhpfasd32458954";
    private String tu1AddressStreet = "an der Luststraße";
    private String tu1HouseNumber = "234g";
    private String tu1Postcode = "10102";
    //test-user2
    private String tu2Username = "floflo:D";
    private String tu2Email = "flololo@exampssleproivider.com";
    private boolean tu2IsPharmacist = false;
    private String tu2PasswordHash = "sdfaijufgahsdfsgfewiuhpfasd32458923";
    private String tu2AddressStreet = "Pierréfitter Straße";
    private String tu2HouseNumber = "15ß";
    private String tu2Postcode = "12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PharmFinderUserRepository userRepository;
    @Autowired
    private PharmFinderAddressRepository addressRepository;
    @Autowired
    private PharmFinderPharmacyRepository pharmacyRepository;
    @Autowired
    private PharmFinderMedicineRepository medicineRepository;

    //TESTS//
    @Test
    void registerNewUserTest1() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));
    }

    @Test
    void registerNewUserTest2() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu2Username)
                .param("email", tu2Email)
                .param("isPharmacist", String.valueOf(tu2IsPharmacist))
                .param("passwordHash", tu2PasswordHash)
                .param("addressStreet", tu2AddressStreet)
                .param("addressHouseNumber", tu2HouseNumber)
                .param("addressPostcode", tu2Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));
    }

    /**
     * Tests if an Exception is thrown when a username is already taken.
     * @throws Exception
     */
    @Test
    void registerNewUserTest3() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder);
        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isConflict());

    }
    @Test
    void getAllUsersTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder);
        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));
    }

    @Test
    void updateUsernameTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/user/update")
                .param("originalUsername", tu1Username)
                .param("originalPassword", tu1PasswordHash)
                .param("username", "testname1")
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
        .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"testname1\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));

    }

    @Test
    void updateEmailTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/user/update")
                .param("originalUsername", tu1Username)
                .param("originalPassword", tu1PasswordHash)
                .param("username", tu1Username)
                .param("email", "tu1Email")
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"tu1Email\",\"pharmacist\":true}]")));
    }

    @Test
    void updateIsPharmacistTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/register")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/user/update")
                .param("originalUsername", tu1Username)
                .param("originalPassword", tu1PasswordHash)
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(false))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/user/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":false}]")));

    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }
}