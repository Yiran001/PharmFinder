package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

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
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private MedicineRepository medicineRepository;

    //TESTS//
    private void setupTu1User() {
        User user = new User();
        user.setUsername(tu1Username);
        user.setEmail(tu1Email);
        user.setPharmacist(tu1IsPharmacist);
        user.setPasswordHash(tu1PasswordHash);
        Address userAddress = new Address(user, tu1AddressStreet, tu1HouseNumber, tu1Postcode);
        addressRepository.save(userAddress);
        user.setUserAddress(userAddress);
        userRepository.save(user);


    }

    /**
     * tests the create operation with test user 1
     *
     * @throws Exception
     */
    @Test
    void createUserTestWithTu1() throws Exception {
        MockHttpServletRequestBuilder builder1 = MockMvcRequestBuilders.post("/users/create")
                .param("username", tu1Username)
                .param("email", tu1Email)
                .param("isPharmacist", String.valueOf(tu1IsPharmacist))
                .param("passwordHash", tu1PasswordHash)
                .param("addressStreet", tu1AddressStreet)
                .param("addressHouseNumber", tu1HouseNumber)
                .param("addressPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));
    }

    /**
     * tests the create operation with test user 2
     *
     * @throws Exception
     */
    @Test
    void createUserTestWithTu2() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users/create")
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
     *
     * @throws Exception
     */
    @Test
    void createUsernameTakenTest() throws Exception {
        setupTu1User();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users/create")
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
                .andExpect(MockMvcResultMatchers.status().isConflict());

    }

    /**
     * tests the correct output of the index operation
     *
     * @throws Exception
     */
    @Test
    void indexUsersTest() throws Exception {
        //SETUP
        setupTu1User();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/index")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"username\":\"marry25\",\"email\":\"marryberry@exampssleproivider.com\",\"pharmacist\":true}]")));
    }

    /**
     * tests the update operation by updating the username
     *
     * @throws Exception
     */
    @Test
    void updateUsernameTest() throws Exception {
        //SETUP
        setupTu1User();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users/update")
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
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        Iterable<User> users = userRepository.findAll();
        List<User> result =
                StreamSupport.stream(users.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertEquals("testname1", result.get(0).getUsername());
    }

    /**
     * tests the update operation by updating the email
     *
     * @throws Exception
     */
    @Test
    void updateEmailTest() throws Exception {
        //SETUP
        setupTu1User();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users/update")
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
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        Iterable<User> users = userRepository.findAll();
        List<User> result =
                StreamSupport.stream(users.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertEquals("tu1Email", result.get(0).getEmail());
    }

    /**
     * tests the update operation by updating the isPharmacist boolean
     *
     * @throws Exception
     */
    @Test
    void updateIsPharmacistTest() throws Exception {
        //SETUP
        setupTu1User();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users/update")
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
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        Iterable<User> users = userRepository.findAll();
        List<User> result =
                StreamSupport.stream(users.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertFalse(result.get(0).isPharmacist());
    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }
}