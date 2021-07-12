package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.Address;
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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    //test-user1
    private String tu1Username = "marry25";
    private String tu1Email = "marryberry@exampssleproivider.com";
    private boolean tu1IsPharmacist = true;
    private String tu1PasswordHash = "sdfaijufgahjipufewiuhpfasd32458954";
    private String tu1AddressStreet = "an der Luststrasse";
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

    public void setupAddressTu1() {
        Address address = new Address();
        address.setStreet(tu1AddressStreet);
        address.setHouseNumber(tu1HouseNumber);
        address.setPostcode(tu1Postcode);
        addressRepository.save(address);
    }

    //TESTS//
    @Test
    public void createAddressTestWithTu1() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/addresses/create")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

    }

    @Test
    public void registerAddressTestWithTu2() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/addresses/create")
                .param("street", tu2AddressStreet)
                .param("houseNumber", tu2HouseNumber)
                .param("postcode", tu2Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));
    }

    @Test
    public void deleteAddressTest() throws Exception {
        //SETUP
        setupAddressTu1();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/addresses/delete")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Deleted")));
        Iterable<Address> addresses = addressRepository.findAll();
        List<Address> result =
                StreamSupport.stream(addresses.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void updateStreetTest() throws Exception {
        //SETUP
        setupAddressTu1();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/addresses/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", "Teststreet")
                .param("newHouseNumber", tu1HouseNumber)
                .param("newPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));
        Iterable<Address> addresses = addressRepository.findAll();
        List<Address> result =
                StreamSupport.stream(addresses.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertEquals("Teststreet", result.get(0).getStreet());
    }

    @Test
    public void updateHouseNumberTest() throws Exception {
        //SETUP
        setupAddressTu1();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/addresses/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", tu1AddressStreet)
                .param("newHouseNumber", "12")
                .param("newPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));

        Iterable<Address> addresses = addressRepository.findAll();
        List<Address> result =
                StreamSupport.stream(addresses.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertEquals("12", result.get(0).getHouseNumber());
    }

    @Test
    public void updatePostcodeTest() throws Exception {
        //SETUP
        setupAddressTu1();

        //TEST
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/addresses/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", tu1AddressStreet)
                .param("newHouseNumber", tu1HouseNumber)
                .param("newPostcode", "12345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));

        Iterable<Address> addresses = addressRepository.findAll();
        List<Address> result =
                StreamSupport.stream(addresses.spliterator(), false)
                        .collect(Collectors.toList());

        Assertions.assertEquals("12345", result.get(0).getPostcode());

    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }

}