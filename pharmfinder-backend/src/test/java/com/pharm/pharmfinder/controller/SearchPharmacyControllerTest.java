package com.pharm.pharmfinder.controller;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.controller.repositories.*;
import com.pharm.pharmfinder.jwt.JwtUserDetailsService;
import com.pharm.pharmfinder.model.MedicineForm;
import com.pharm.pharmfinder.model.Pharmacy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class SearchPharmacyControllerTest {
    //pharmacist1
    private String username1 = "max";
    private String emailU1 = "max@gmail.com";
    private boolean isPU1 = true;
    private String passwordU1 = "4384754";
    private String streetU1 = "Kreuzstrasse";
    private String houseNumberU1 = "24";
    private String postcodeU1 = "16225";
    private String lat = "53.83543";
    private String lng="49.89322";
    //pharmacist2
    private String usernameP2 = "hans";
    private String emailP2 = "hans@gmail.com";
    private boolean isP2 = true;
    private String passwordP2 = "43323484754";
    private String streetP2 = "Grabowstrasse";
    private String houseNumberP2 = "3";
    private String postcodeP2 = "16225";
    private String latP2 = "53.49095";
    private String lngP2="50.0001";
    //logged in user
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
    @Autowired
    private SearchPharmacyController searchPharmacyController;


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

    /**
     * User wird registriert, mit neuen Parametern latitude und longitude von der Adresse des Users
     * @throws Exception
     */
    @Test
    public void should_add_user() throws Exception {
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

    /**
     * testet searchPharmacies function von searchPharmacyController
     * @throws Exception
     */
    @Test
    public void testCalcFunctionGood01() throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        String jwtU1 = authenticate(username1, passwordU1);
        addMedicine1(username1, jwtU1);
        ArrayList<Pharmacy> p = new ArrayList<>();
        Iterable<Pharmacy> pharms = pharmacyRepository.findAll();
        for(Pharmacy pharm: pharms){
            p.add(pharm);
        }
        HashMap<Pharmacy,Double> hashMapP=searchPharmacyController.searchPharmacies(p,50.00,51.00);
        Assertions.assertEquals(1,hashMapP.size());
    }

    /**
     * 6 Apotheken haben Medikament aber nur die 5 nähesten werden per HasMap zurückgegeben und Apotheke 6  ist nicht enthalten
     * @throws Exception
     */
    @Test
    public void testCalcFunctionEdge01() throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username1+"2",emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username1+"3",emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username1+"4",emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username1+"5",emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username1+"6",emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,"0","0");
        //pharmacist01
        String jwtU1 = authenticate(username1, passwordU1);
        addMedicine1(username1, jwtU1);
        //pharmacist02
        String jwt2 = authenticate(username1+"2", passwordU1);
        addMedicine1(username1+"2", jwt2);
        //pharmacist03
        String jwt3 = authenticate(username1+"3", passwordU1);
        addMedicine1(username1+"3", jwt3);
        //pharmacist04
        String jwt4 = authenticate(username1+"4", passwordU1);
        addMedicine1(username1+"4", jwt4);
        //pharmacist05
        String jwt5 = authenticate(username1+"5", passwordU1);
        addMedicine1(username1+"5", jwt5);
        //pharmacist06
        String jwt6 = authenticate(username1+"6", passwordU1);
        addMedicine1(username1+"6", jwt6);
        ArrayList<Pharmacy> p = new ArrayList<>();
        Iterable<Pharmacy> pharms = pharmacyRepository.findAll();
        for(Pharmacy pharm: pharms){
            p.add(pharm);
        }
        HashMap<Pharmacy,Double> hashMapP=searchPharmacyController.searchPharmacies(p,50.00,51.00);
        Assertions.assertEquals(5,hashMapP.size());
        boolean farthestPharmNotInMap=false;
        for(Pharmacy p3: hashMapP.keySet()){
            if(p3.getPharmacyName().equals(username1+"6"))
                farthestPharmNotInMap=true;
        }
        Assertions.assertFalse(farthestPharmNotInMap);
    }

    /**
     * es wird nach Apotheke gesucht, die ein bestimmtes Medikament hat und gefunden
     * zurückgegeben werden die wichtigsten Attribute der Apotheke und die Distanz zu ihr und dem user
     * @throws Exception
     */
    @Test
    public void searchForPharmacyGood01() throws Exception {
        String expectedString="\"pharmacyname\":\""+username1+"\",\"latitude\":\""+lat+"\",\"longitude\":\""+lng+"\",\"username\":\""+username1+"\",\"street\":\""+streetU1+"\",\"houseNumber\":\""+houseNumberU1+"\",\"postcode\":\""+postcodeU1+"\",\"dist\":\"";
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        String jwtU1 = authenticate(username1, passwordU1);
        addMedicine1(username1, jwtU1);
        addUserViaPostRequest(username2,emailU2,isPU2,passwordU2,streetU2,houseNumberU2,postcodeU2,lat2,lng2);
        String jwtU2 = authenticate(username1, passwordU1);
        MockHttpServletRequestBuilder builder =searchForPharmacy(pznM1,lat2,lng2,jwtU2);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string(containsString(expectedString+String.valueOf(this.calDist(lat,lng,lat2,lng2)))));

    }

    /**
     * 2 Apotheken mit gesuchtem medikament sind vorhanden
     * @throws Exception
     */
    @Test
    public void searchForPharmaciesGood02() throws Exception {
        String expectedStringP1="\"pharmacyname\":\""+username1+"\",\"latitude\":\""+lat+"\",\"longitude\":\""+lng+"\",\"username\":\""+username1+"\",\"street\":\""+streetU1+"\",\"houseNumber\":\""+houseNumberU1+"\",\"postcode\":\""+postcodeU1+"\",\"dist\":\"";
        String expectedStringP2="\"pharmacyname\":\""+usernameP2+"\",\"latitude\":\""+latP2+"\",\"longitude\":\""+lngP2+"\",\"username\":\""+usernameP2+"\",\"street\":\""+streetP2+"\",\"houseNumber\":\""+houseNumberP2 +"\",\"postcode\":\""+postcodeP2+"\",\"dist\":\"";
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        String jwtU1 = authenticate(username1, passwordU1);
        addMedicine1(username1, jwtU1);
        addUserViaPostRequest(usernameP2,emailP2,isP2,passwordP2,streetP2,houseNumberP2,postcodeP2,latP2,lngP2);
        String jwtP2 = authenticate(usernameP2, passwordP2);
        addMedicine1(usernameP2, jwtP2);
        addUserViaPostRequest(username2,emailU2,isPU2,passwordU2,streetU2,houseNumberU2,postcodeU2,lat2,lng2);
        String jwtU2 = authenticate(username1, passwordU1);
        MockHttpServletRequestBuilder builder =searchForPharmacy(pznM1,lat2,lng2,jwtU2);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * wenn Apotheke gesucht wird, aber keine Apotheke das Medikament hat
     * @throws Exception MedicineNotFoundException mit Statuscode 404
     */
    @Test
    public void  searchForPharmacyBad01()throws Exception {
        addUserViaPostRequest(username1,emailU1,isPU1,passwordU1,streetU1,houseNumberU1,postcodeU1,lat,lng);
        addUserViaPostRequest(username2,emailU2,isPU2,passwordU2,streetU2,houseNumberU2,postcodeU2,lat2,lng2);
        String jwtU2 = authenticate(username1, passwordU1);
        MockHttpServletRequestBuilder builder =searchForPharmacy(pznM1,lat2,lng2,jwtU2);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

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
    private MockHttpServletRequestBuilder searchForPharmacy(String pzn,String latitude,String longitude,String jwt)throws Exception{
        MockHttpServletRequestBuilder builder = buildSearchPharmacyPostRequest
                (pzn, latitude,longitude)
                .header("Authorization", generateAuthHeader(jwt));
        return builder;
    }
    private Double calDist(String lat1,String lng1,String lat2,String lng2){
        double lat= Double.parseDouble(lat1);
        double latP= Double.parseDouble(lat2);
        double lng= Double.parseDouble(lng1);
        double lngP= Double.parseDouble(lng2);
        double latD = (lat + latP) / 2 * 0.01745;
        double dx = 111.3 * Math.cos(degToRad(latD)) * (lng - lngP);
        double dy = 111.3 * (lat - latP);
        double dist = Math.sqrt(dx * dx + dy*dy);
        return dist;
    }
    private double degToRad(double x){
        return  x * Math.PI/180;
    }


}
