package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.AddressRepository;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.*;
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

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class MedicineControllerTest {
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
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private MedicineRepository medicineRepository;

    @AfterEach
    void tearDown() {
        pharmacyRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        medicineRepository.deleteAll();
    }


    @Test
    void should_save_multiple_medicines_in_one_pharmacy() throws Exception {
        addPharmacistUser1();
        addMedicine(pzn1, friendlyName, medicineForm, username1);
        addMedicine2(username1);
        getAndVerifyResponse(username1, "pzn='" + pzn1 + "'");
        getAndVerifyResponse(username1, "pzn='124'");
    }

    @Test
    void should_not_return_medicines_from_different_user() throws Exception {
        addPharmacistUser1();
        addPharmacistUser2();
        addMedicine(pzn1, friendlyName, medicineForm, username1);
        MockHttpServletRequestBuilder builder = get("/medicines/index")
                .param("username", username2)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("No medicines registered")));
    }

    @Test
    void should_not_return_medicine_after_deleting() throws Exception {
        addPharmacistUser1();
        addMedicine(pzn1, friendlyName, medicineForm, username1);
        deregisterMedicine(pzn1, username1);
        MockHttpServletRequestBuilder builder = get("/medicines/index")
                .param("username", username1)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(equalTo("No medicines registered")));
    }

    @Test
    void should_change_amount_on_update() throws Exception {
        int newAmount = 15;
        addPharmacistUser1();
        addMedicine(pzn1, friendlyName, medicineForm, username1);
        updateMedicine(pzn1, friendlyName, medicineForm, username1, newAmount);
        getAndVerifyResponse(username1, "Amount: " + newAmount);
    }

    @Test
    void should_change_friendlyName_on_update() throws Exception {
        String newFriendlyName = "newFriendlyName";
        addPharmacistUser1();
        addMedicine(pzn1, friendlyName, medicineForm, username1);
        updateMedicine(pzn1, newFriendlyName, medicineForm, username1, 0);
        getAndVerifyResponse(username1, "friendlyName='" + newFriendlyName + "'");
    }

    @Test
    void should_save_one_medicine_to_multiple_pharmacys() throws Exception {
        String username = "name";
        String username2 = "name2";
        addPharmacistUser1();
        addPharmacistUser2();
        addMedicine1(username);
        addMedicine1(username2);
        getAndVerifyResponse(username, "pzn='" + pzn1 + "'");
        getAndVerifyResponse(username2, "pzn='" + pzn1 + "'");
    }

    private void addPharmacistUser1() {
        User user = new User();
        user.setUsername(username1);
        user.setEmail(email1);
        user.setPharmacist(true);
        user.setPasswordHash(password1);
        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        Address address = new Address(userSet, street1, houseNumber1, postcode1);
        user.setUserAddress(address);
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyAddress(address);
        pharmacy.setPharmacyName(username1);
        pharmacy.setOwner(user);
        pharmacy.setPharmacyMedicines(new HashSet<PharmacyMedicine>());
        addressRepository.save(address);
        userRepository.save(user);
        pharmacyRepository.save(pharmacy);
    }

    private void addPharmacistUser2() {
        User user = new User();
        user.setUsername(username2);
        user.setEmail(email2);
        user.setPharmacist(true);
        user.setPasswordHash(password2);
        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        Address address = new Address(userSet, street2, houseNumber2, postcode2);
        user.setUserAddress(address);
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyAddress(address);
        pharmacy.setPharmacyName(username2);
        pharmacy.setOwner(user);
        pharmacy.setPharmacyMedicines(new HashSet<PharmacyMedicine>());
        addressRepository.save(address);
        userRepository.save(user);
        pharmacyRepository.save(pharmacy);
    }

    private void updateMedicine(String pzn, String friendlyName, MedicineForm medicineForm, String username, int amount) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePutRequest
                (pzn, friendlyName, String.valueOf(medicineForm), username, amount);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine1(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn1, friendlyName, String.valueOf(medicineForm), username, 0);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine2(String username) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn2, friendlyName2, String.valueOf(medicineForm2), username, 0);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
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

    private void deregisterMedicine(String pzn, String username) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("/medicines/delete")
                .param("pzn", pzn)
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addMedicine(String pzn, String friendlyName, MedicineForm medicineForm, String username) {
        Medicine medicine = new Medicine(pzn, new HashSet<PharmacyMedicine>(), friendlyName, medicineForm);
        Pharmacy pharmacy = getPharmacy(username);
        PharmacyMedicine pharmacyMedicine = new PharmacyMedicine(pharmacy, medicine, 0);
        savePharmacyMedicineToMedicine(medicine, pharmacyMedicine);
        savePharmacyMedicineToEmptyPharmacy(pharmacy, pharmacyMedicine);
        medicineRepository.save(medicine);
        pharmacyRepository.save(pharmacy);
    }

    private void getAndVerifyResponse(String username, String expectedOutput) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/medicines/index")
                .param("username", username)
                .accept(MediaType.APPLICATION_JSON_VALUE);
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
