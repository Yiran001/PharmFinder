
package com.pharm.pharmfinder.jwt;

import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.model.Role;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!user.isEnabled())
            return null;

        ArrayList<Role> authorities = new ArrayList<>();
        String[] authorityStrings = user.getAuthorities().split(",");
        for (String string : authorityStrings) {
            authorities.add(new Role(string));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPasswordHash(),
                authorities);
    }
}


