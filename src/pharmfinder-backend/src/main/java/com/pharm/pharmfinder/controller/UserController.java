package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam String username, @RequestParam String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPharmacist(true);
        user.setPasswordHash("abc");
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllPharmacyUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
