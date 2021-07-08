package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.PharmFinderAddressRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderPharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.PharmFinderUserRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class PharmFinderAddressControllerTest {

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
    private PharmFinderUserRepository userRepository;
    @Autowired
    private PharmFinderAddressRepository addressRepository;
    @Autowired
    private PharmFinderPharmacyRepository pharmacyRepository;
    @Autowired
    private PharmFinderMedicineRepository medicineRepository;

    //TESTS//
    @Test
    public void registerAddressTest1() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/address/add")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));
    }

    @Test
    public void registerAddressTest2() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/address/add")
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
        MockHttpServletRequestBuilder builder1 = MockMvcRequestBuilders.put("/address/add")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));


        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.delete("/address/delete")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Deleted")));

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("[]")));

    }

    @Test
    public void updateAddressTest1() throws Exception {
        MockHttpServletRequestBuilder builder1 = MockMvcRequestBuilders.put("/address/add")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/address/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", "Teststreet")
                .param("newHouseNumber", tu1HouseNumber)
                .param("newPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"Teststreet\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));
    }

    @Test
    public void updateAddressTest2() throws Exception {
        MockHttpServletRequestBuilder builder1 = MockMvcRequestBuilders.put("/address/add")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/address/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", tu1AddressStreet)
                .param("newHouseNumber", "12")
                .param("newPostcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"12\",\"postcode\":\"10102\"}]")));
    }

    @Test
    public void updateAddressTest3() throws Exception {
        MockHttpServletRequestBuilder builder1 = MockMvcRequestBuilders.put("/address/add")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Saved")));

        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder2)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"10102\"}]")));

        MockHttpServletRequestBuilder builder3 = MockMvcRequestBuilders.post("/address/update")
                .param("street", tu1AddressStreet)
                .param("houseNumber", tu1HouseNumber)
                .param("postcode", tu1Postcode)
                .param("newStreet", tu1AddressStreet)
                .param("newHouseNumber", tu1HouseNumber)
                .param("newPostcode", "12345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder3)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("Updated")));

        MockHttpServletRequestBuilder builder4 = MockMvcRequestBuilders.get("/address/all")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder4)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString("\"addressUsers\":[],\"street\":\"an der Luststrasse\",\"houseNumber\":\"234g\",\"postcode\":\"12345\"}]")));
    }

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }

}