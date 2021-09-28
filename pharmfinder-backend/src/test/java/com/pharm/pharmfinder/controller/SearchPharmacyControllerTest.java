package com.pharm.pharmfinder.controller;


import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchPharmacyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Test
    void testDistancePharmacy(){

    }




}
