
package com.pharm.pharmfinder.jwt;

import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.controller.repositories.VerificationTokenRepository;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
//            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        if (!user.isEnabled())
            return null;

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(),
                new ArrayList<>());
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
}
