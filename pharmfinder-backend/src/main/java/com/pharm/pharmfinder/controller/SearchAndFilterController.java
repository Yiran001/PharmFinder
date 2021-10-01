package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.search_and_filter.ListResponse;
import com.pharm.pharmfinder.model.search_and_filter.MedicineView;
import com.pharm.pharmfinder.model.search_and_filter.SearchAndFilterRequest;
import com.pharm.pharmfinder.model.search_and_filter.service.SearchAndFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping(path = "/search_and_filter")
public class SearchAndFilterController {

    @Autowired
    private SearchAndFilterService searchAndFilterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping(path="/get")
    public @ResponseBody List<MedicineView> searchAndFilter(HttpServletRequest httpRequest){
        String username = httpRequest.getParameter("username");
        SearchAndFilterRequest searchAndFilterRequest = new SearchAndFilterRequest();
        searchAndFilterRequest.setUsername(username);
        searchAndFilterRequest.setPzn(httpRequest.getParameter("pzn"));
        searchAndFilterRequest.setFriendlyName(httpRequest.getParameter("friendlyName"));
        searchAndFilterRequest.setMedicineForm(httpRequest.getParameter("medicineForm"));
        searchAndFilterRequest.setAmount(httpRequest.getParameter("amount"));
        searchAndFilterRequest.setSortBy(httpRequest.getParameter("sortBy"));
        searchAndFilterRequest.setDescending(Boolean.parseBoolean(httpRequest.getParameter("descending")));
        checkAuthorization(username, httpRequest);
        List<MedicineView> list = searchAndFilterService.getMedicines(searchAndFilterRequest);
        return new ArrayList<>(list);
    }

    private void checkAuthorization(String username, HttpServletRequest httpRequest){
        String jwt = httpRequest.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthorities().contains("MEDICINE_ADMIN"))
            return;
        if (!username.equals(jwtUsername))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong username");
    }
}
