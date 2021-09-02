
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
//            throw new UsernameNotFoundException("User not found with username: " + username);
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

//    @Transactional
//    public void create(FullUserAdminRequest request) throws ValidationException {
//        if (userRepository.findByUsername(request.getUsername()) == null) {
//            throw new ValidationException("Username exists!");
//        }
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPharmacist(request.isPharmacist());
//        user.setEnabled(true);
//        user.setPasswordHash(bcryptEncoder.encode(request.getPassword()));
//        user.setAuthorities("");
//        userRepository.save(user);
//        Address userAddress = new Address(user, request.getAddressStreet(), request.getAddressHouseNumber(),
//                request.getAddressPostcode());
//        addressRepository.save(userAddress);
//        user.setUserAddress(userAddress);
//        userRepository.save(user);
//        Pharmacy pharmacy = new Pharmacy();
//        pharmacy.setPharmacyAddress(userAddress);
//        pharmacy.setPharmacyName(request.getUsername());
//        pharmacy.setOwner(user);
//        pharmacyRepository.save(pharmacy);
//    }
//
//    @Transactional
//    public void update(FullUserAdminRequest request) {
//        User user = userRepository.findByUsername(request.getUsername());
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPasswordHash(bcryptEncoder.encode(request.getPassword()));
//        user.setPharmacist(request.isPharmacist());
//        Address address = user.getUserAddress();
//        address.setStreet(request.getAddressStreet());
//        address.setHouseNumber(request.getAddressHouseNumber());
//        address.setPostcode(request.getAddressPostcode());
////        addressRepository.
//    }
//}
