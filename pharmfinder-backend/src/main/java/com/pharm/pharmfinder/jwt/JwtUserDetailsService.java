
package com.pharm.pharmfinder.jwt;

import com.pharm.pharmfinder.controller.repositories.*;
import com.pharm.pharmfinder.model.Address;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.Role;
import com.pharm.pharmfinder.model.User;
import com.pharm.pharmfinder.model.mail.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

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

    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    public void deleteVerificationToken(VerificationToken token){
        verificationTokenRepository.deleteById(token.getId());
    }

    public void deleteUserByUsername(String username){
        User user = userRepository.findByUsername(username);

        if (System.getenv("REGISTRATION_EMAIL") != null && System.getenv("REGISTRATION_EMAIL").equalsIgnoreCase("true")){
            VerificationToken token = verificationTokenRepository.findByUser(user);
//            token already gets deleted, when confirmation comes in
            if (token != null)
                verificationTokenRepository.deleteById(token.getId());
        }

        Pharmacy pharmacy = pharmacyRepository.findByUser(user);
        if (pharmacy != null)
            pharmacyRepository.delete(pharmacy);

        Set<User> addressUsers = user.getUserAddress().getAddressUsers();
        if (addressUsers.size() <= 1)
            addressRepository.delete(user.getUserAddress());
        else addressUsers.remove(user);

        userRepository.delete(user);
    }
}


