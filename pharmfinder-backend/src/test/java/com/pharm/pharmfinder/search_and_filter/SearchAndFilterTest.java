package com.pharm.pharmfinder.search_and_filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pharm.pharmfinder.model.MedicineForm;
import com.pharm.pharmfinder.model.search_and_filter.SearchAndFilterRequest;
import org.junit.jupiter.api.Assertions;
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
import java.util.UUID;

import static com.pharm.pharmfinder.model.search_and_filter.mapper.JsonHelper.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchAndFilterTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    //    User 1
    String baseUsername = "test_name";
    String email1 = "PharmacyFinderHTW@gmail.com";
    String password1 = "password";
    String street1 = "streetOne";
    String houseNumber1 = "1";
    String postcode1 = "12345";
    //    Medicine 1
    public String basePzn = "123";
    public String friendlyName = "Aspirin";
    public String baseFriendlyName = "Aspirin";
    public MedicineForm medicineForm = MedicineForm.PILL;

    @Autowired
    public SearchAndFilterTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void should_respond_with_medicine_attributes() throws Exception {
        String username = baseUsername + "0";
        String pzn = basePzn + "0";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn, friendlyName, medicineForm, 1);
        searchInRequest(username, jwt, pzn, friendlyName, medicineForm.toString(), "1");
    }

    @Test
    void should_sort_by_pzn() throws Exception {
        String username = baseUsername + "1";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        String pzn3 = basePzn + "3";
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, 1);
        String pzn1 = basePzn + "1";
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, 1);
        String pzn2 = basePzn + "2";
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm, 1);
        checkSorting(username, jwt, "pzn", pzn1, pzn2, pzn3);
    }

    @Test
    void should_sort_by_friendlyName() throws Exception {
        String username = baseUsername + "2";
        String pzn1 = basePzn + "4";
        String pzn2 = basePzn + "5";
        String pzn3 = basePzn + "6";

        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        String friendlyName3 = baseFriendlyName + "c";
        addMedicine(username, jwt, pzn1, friendlyName3, medicineForm, 1);
        String friendlyName1 = baseFriendlyName + "a";
        addMedicine(username, jwt, pzn2, friendlyName1, medicineForm, 1);
        String friendlyName2 = baseFriendlyName + "b";
        addMedicine(username, jwt, pzn3, friendlyName2, medicineForm, 1);
        checkSorting(username, jwt, "friendlyName", friendlyName1, friendlyName2, friendlyName3);
    }

    @Test
    void should_sort_by_medicineForm() throws Exception {
        String username = baseUsername + "3";
        String pzn1 = basePzn + "7";
        String pzn2 = basePzn + "8";
        String pzn3 = basePzn + "9";
        MedicineForm  medicineForm1 = MedicineForm.INHALATION;
        MedicineForm  medicineForm2 = MedicineForm.PILL;
        MedicineForm  medicineForm3 = MedicineForm.POWDER;
        System.out.println(String.valueOf(medicineForm1));

        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm2, 1);
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm3, 1);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm1, 1);
        checkSorting(username, jwt, "medicineForm", String.valueOf(medicineForm1), String.valueOf(medicineForm2),
                String.valueOf(medicineForm3));
    }

    @Test
    void should_sort_by_amount() throws Exception {
        String username = baseUsername + "4";
        String pzn1 = basePzn + "10";
        String pzn2 = basePzn + "11";
        String pzn3 = basePzn + "12";
        int amount1 = 4;
        int amount2 = 6;
        int amount3 = 42;
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, amount2);
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm, amount1);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, amount3);
        checkSorting(username, jwt, "amount", String.valueOf(amount1), String.valueOf(amount2),
                String.valueOf(amount3));
    }

    @Test
    void should_search_for_friendlyName_and_sort_for_amount() throws Exception {
        String username = baseUsername + "5";
        String pzn1 = basePzn + "13";
        String pzn2 = basePzn + "14";
        String pzn3 = basePzn + "15";
        String pzn4 = basePzn + "16";
        String pzn5 = basePzn + "17";
        int amount1 = 4;
        int amount2 = 6;
        int amount3 = 42;
        String differentFriendlyName = "Ibuprofen";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn1, differentFriendlyName, medicineForm, amount2);
        addMedicine(username, jwt, pzn4, friendlyName, medicineForm, amount2);
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm, amount1);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, amount3);
        addMedicine(username, jwt, pzn5, differentFriendlyName, medicineForm, amount3);
        String stringResult = searchFriendlyNameAndCheckSorting(username, jwt, friendlyName, "amount", String.valueOf(amount1), String.valueOf(amount2),
                String.valueOf(amount3));
//        only medicines with the searched for name get returned and sorted
        Assertions.assertFalse(stringResult.contains(differentFriendlyName));
    }

    @Test
    void should_search_for_medicineForm_and_sort_for_pzn() throws Exception {
        String username = UUID.randomUUID().toString();
        String pzn1 = basePzn + "18";
        String pzn2 = basePzn + "19";
        String pzn3 = basePzn + "20";
        String pzn4 = basePzn + "21";
        String pzn5 = basePzn + "22";
        int amount1 = 4;
        int amount2 = 6;
        int amount3 = 42;
        MedicineForm differentMedicineForm = MedicineForm.OTHER;
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn2, friendlyName, differentMedicineForm, amount2);
        addMedicine(username, jwt, pzn5, friendlyName, medicineForm, amount3);
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, amount2);
        addMedicine(username, jwt, pzn4, friendlyName, differentMedicineForm, amount3);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, amount1);
        String stringResult = searchMedicineFormAndCheckSorting(username, jwt, String.valueOf(medicineForm),
                "pzn", pzn1, pzn3, pzn5);
//        only medicines with the searched for name get returned and sorted
        Assertions.assertFalse(stringResult.contains(String.valueOf(differentMedicineForm)));
    }

    @Test
    void should_search_for_medicineForm_and_friendlyName_and_sort_for_pzn() throws Exception {
        String username = UUID.randomUUID().toString();
        String pzn1 = basePzn + "23";
        String pzn2 = basePzn + "24";
        String pzn3 = basePzn + "25";
        String pzn4 = basePzn + "26";
        String pzn5 = basePzn + "27";
        String pzn6 = basePzn + "28";
        int amount1 = 4;
        int amount2 = 6;
        int amount3 = 42;
        MedicineForm differentMedicineForm = MedicineForm.OTHER;
        String differentFriendlyName = "Ibuprofen";
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        addMedicine(username, jwt, pzn2, friendlyName, differentMedicineForm, amount2);
        addMedicine(username, jwt, pzn5, friendlyName, medicineForm, amount3);
        addMedicine(username, jwt, pzn6, differentFriendlyName, medicineForm, amount3);
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, amount2);
        addMedicine(username, jwt, pzn4, friendlyName, differentMedicineForm, amount3);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, amount1);
        String stringResult = searchMedicineFormAndFriendlyNameAndCheckSorting(username, jwt, String.valueOf(medicineForm),
                friendlyName, "pzn", pzn1, pzn3, pzn5);
//        only medicines with the searched for name get returned and sorted
        Assertions.assertFalse(stringResult.contains(String.valueOf(differentMedicineForm)));
        Assertions.assertFalse(stringResult.contains(differentFriendlyName));
    }

    @Test
    void should_do_descending_sort_by_pzn() throws Exception {
        String username = UUID.randomUUID().toString();
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        String pzn1 = basePzn + "29";
        String pzn2 = basePzn + "30";
        String pzn3 = basePzn + "31";
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, 1);
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, 1);
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm, 1);
        checkSortingDescending(username, jwt, "pzn", pzn1, pzn2, pzn3);
    }

    @Test
    void should_smaller_or_equal_given_amount() throws Exception {
        String username = UUID.randomUUID().toString();
        addUserViaPostRequest(username);
        String jwt = authenticate(username, password1);
        String pzn1 = basePzn + "32";
        String pzn2 = basePzn + "33";
        String pzn3 = basePzn + "34";
        addMedicine(username, jwt, pzn1, friendlyName, medicineForm, 1);
        addMedicine(username, jwt, pzn2, friendlyName, medicineForm, 2);
        addMedicine(username, jwt, pzn3, friendlyName, medicineForm, 3);
        checkSortingDescending(username, jwt, "pzn", pzn1, pzn2, pzn3);
        String result = searchForAmount(username, jwt, "2");
        Assertions.assertTrue(result.contains(pzn1));
        Assertions.assertTrue(result.contains(pzn2));
        Assertions.assertFalse(result.contains(pzn3));
    }

//    string1 should get returned first, string3 last
    private void checkSorting(String username, String jwt, String sortBy, String string1, String string2, String string3) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setSortBy(sortBy);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String stringResult = result.getResponse().getContentAsString();
        System.out.println(stringResult);
        int index1 = stringResult.indexOf(string1);
        int index2 = stringResult.indexOf(string2);
        int index3 = stringResult.indexOf(string3);
        Assertions.assertTrue(index1 < index2 && index2 < index3);
    }

    //    string1 should get returned first, string3 last
    private void checkSortingDescending(String username, String jwt, String sortBy, String string1, String string2, String string3) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setSortBy(sortBy);
        request.setDescending(true);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String stringResult = result.getResponse().getContentAsString();
        System.out.println(stringResult);
        int index1 = stringResult.indexOf(string1);
        int index2 = stringResult.indexOf(string2);
        int index3 = stringResult.indexOf(string3);
        Assertions.assertTrue(index1 > index2 && index2 > index3);
    }

    //    string1 should get returned first, string3 last
    private String searchFriendlyNameAndCheckSorting(String username, String jwt, String searchFor, String sortBy, String string1, String string2, String string3) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setFriendlyName(searchFor);
        request.setSortBy(sortBy);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String stringResult = result.getResponse().getContentAsString();
        System.out.println(stringResult);
        int index1 = stringResult.indexOf(string1);
        int index2 = stringResult.indexOf(string2);
        int index3 = stringResult.indexOf(string3);
        Assertions.assertTrue(index1 < index2 && index2 < index3);
        return stringResult;
    }

    //    string1 should get returned first, string3 last
    private String searchMedicineFormAndCheckSorting(String username, String jwt, String searchFor, String sortBy, String string1, String string2, String string3) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setMedicineForm(searchFor);
        request.setSortBy(sortBy);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String stringResult = result.getResponse().getContentAsString();
        System.out.println(stringResult);
        int index1 = stringResult.indexOf(string1);
        int index2 = stringResult.indexOf(string2);
        int index3 = stringResult.indexOf(string3);
        Assertions.assertTrue(index1 < index2 && index2 < index3);
        return stringResult;
    }

    //    string1 should get returned first, string3 last
    private String searchMedicineFormAndFriendlyNameAndCheckSorting(String username, String jwt, String medicineForm, String friendlyName, String sortBy, String string1, String string2, String string3) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setMedicineForm(medicineForm);
        request.setFriendlyName(friendlyName);
        request.setSortBy(sortBy);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String stringResult = result.getResponse().getContentAsString();
        System.out.println(stringResult);
        int index1 = stringResult.indexOf(string1);
        int index2 = stringResult.indexOf(string2);
        int index3 = stringResult.indexOf(string3);
        Assertions.assertTrue(index1 < index2 && index2 < index3);
        return stringResult;
    }

    private String searchForAmount(String username, String jwt, String amount) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setAmount(amount);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        MvcResult result = this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private void searchInRequest(String username, String jwt, String pzn, String friendlyName, String medicineForm, String amount) throws Exception {
        SearchAndFilterRequest request = new SearchAndFilterRequest();
        request.setUsername(username);
        request.setPzn(pzn);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/search_and_filter/get")
                .content(toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString(pzn)))
                .andExpect(content().string(containsString(friendlyName)))
                .andExpect(content().string(containsString(medicineForm)))
                .andExpect(content().string(containsString("\"amount\":" + amount)))
                .andReturn();
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

    private void addMedicine(String username, String jwt, String pzn, String friendlyName, MedicineForm medicineForm, int amount) throws Exception {
        MockHttpServletRequestBuilder builder = buildMedicinePostRequest
                (pzn, friendlyName, String.valueOf(medicineForm), username, amount)
                .header("Authorization", "Bearer " + jwt);
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk());
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
