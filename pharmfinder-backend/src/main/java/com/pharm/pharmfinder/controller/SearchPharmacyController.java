package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.exceptions.MedicineNotFoundException;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.Pharmacy;
import com.pharm.pharmfinder.model.PharmacyMedicine;
import com.pharm.pharmfinder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@CrossOrigin
@RequestMapping(path = "/search")
public class SearchPharmacyController {
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PharmacyMedicineRepository pharmacyMedicineRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    /**
     * handles exception, that occurs when wanted medicine in no pharmacy
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such username")
    @ExceptionHandler(MedicineNotFoundException.class)
    public void noMedicineFoundException() {
    }

    // einfachere Pharmacy für frontend ohne Medikamentenbestand etc.
    public class frontendPharmacy{
        private String pharmacyname;
        private String latitude;
        private String longitude;
        private String username;
        private String street;
        private String houseNumber;
        private String postcode;
        private String dist;

        public frontendPharmacy(String pharmacyname, String latitude, String longitude, String username, String street, String houseNumber, String postcode,String dist) {
            this.pharmacyname = pharmacyname;
            this.latitude = latitude;
            this.longitude = longitude;
            this.username = username;
            this.street = street;
            this.houseNumber = houseNumber;
            this.postcode = postcode;
            this.dist=dist;
        }
        public String getPharmacyname() {
            return pharmacyname;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getUsername() {
            return username;
        }

        public String getStreet() {
            return street;
        }

        public String getHouseNumber() {
            return houseNumber;
        }

        public String getPostcode() {
            return postcode;
        }


        public String getDist() {
            return dist;
        }
    }

    @GetMapping(path = "/pharmacy", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<frontendPharmacy> search(HttpServletRequest request, @RequestParam String pzn, @RequestParam String latitude, @RequestParam String longitude) throws MedicineNotFoundException {

        ArrayList<Pharmacy> pharmacies = new ArrayList<>();
        HashMap<Pharmacy, Double> nearbyPharmacies;
        List<frontendPharmacy> pharList = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        Iterable<PharmacyMedicine> pharmacyMedicines = pharmacyMedicineRepository.findAll();
        for(PharmacyMedicine p : pharmacyMedicines){
            if(p.getMedicine().getPzn().equals(pzn)){
                pharmacies.add(p.getPharmacy());
            }
        }
        if(pharmacies.size()>0){
            nearbyPharmacies=searchPharmacies(pharmacies,Double.parseDouble(latitude),Double.parseDouble(longitude));
        }else{
            throw new MedicineNotFoundException("Medicine was not found");

        }
        for(Pharmacy p: nearbyPharmacies.keySet()){
            result.append("Pharmacy: ").append(p.getPharmacyID()).append(" Latitude: ").append(p.getLat()).append(" Longitude: ").append(p.getLng()).append(" Distance: ").append(nearbyPharmacies.get(p));
            frontendPharmacy fP = new frontendPharmacy(p.getPharmacyName(),p.getLat(),p.getLng(),p.getUser().getUsername(),p.getPharmacyAddress().getStreet(),p.getPharmacyAddress().getHouseNumber(),p.getPharmacyAddress().getPostcode(),String.valueOf(nearbyPharmacies.get(p)));
            pharList.add(fP);
        }
        return pharList;
    }

    /**
     *
     * @param pharmacies liste mit pharmacies, die gesuchtes Medikament auf Lager haben
     * @param lat latitude des Users, der Apotheke sucht
     * @param lng longitude des Users, der Apotheke sucht
     * @return die ggf. 5 nähesten Apotheken werden als Hashmap mit der Entfernung zurückgegeben
     */
    private HashMap<Pharmacy,Double> searchPharmacies(ArrayList<Pharmacy> pharmacies, double lat,double lng) {

        HashMap<Pharmacy,Double> nearbyPharmacies = new HashMap<Pharmacy, Double>();
        for(Pharmacy p: pharmacies){

            //annahme dass apotheke nicht zu weit entfernt ist + kürzere rechenzeit, deshalb satz des pythagoras angewendet
            double pharLng=Double.parseDouble(p.getLng());
            double pharLat=Double.parseDouble(p.getLat());
            double latD = (lat + pharLat) / 2 * 0.01745;
            double dx = 111.3 * Math.cos(degToRad(latD)) * (lng - pharLng);
            double dy = 111.3 * (lat - pharLat);
            double dist = Math.sqrt(dx * dx + dy*dy);

                if(nearbyPharmacies.size()<5)
                    nearbyPharmacies.put(p,dist);
                else {
                    Double longestDist=-1.0;
                    Pharmacy farthestPharmacy=null;
                    for (Pharmacy p2 : nearbyPharmacies.keySet()) {
                        Double distance = nearbyPharmacies.get(p2);
                        if (longestDist <= distance) {
                            longestDist = distance;
                            farthestPharmacy=p2;
                        }
                    }
                    if(longestDist>dist){
                        nearbyPharmacies.remove(farthestPharmacy);
                        nearbyPharmacies.put(p,dist);
                    }
                }

        }
        return nearbyPharmacies;
    }
    private double degToRad(double x){
        return  x * Math.PI/180;
    }

    private void checkAuthorization(HttpServletRequest request, String username){
        String jwt = request.getHeader("Authorization").substring(7);
        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
        User manipulatingUser = userRepository.findByUsername(jwtUsername);
        if (manipulatingUser.getAuthorities().contains("USER_ADMIN"))
            return;
        if (!username.equals(jwtUsername))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong username");
    }
}
