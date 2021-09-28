package com.pharm.pharmfinder.controller;

import com.pharm.pharmfinder.controller.exceptions.MedicineNotFoundException;
import com.pharm.pharmfinder.controller.repositories.MedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyMedicineRepository;
import com.pharm.pharmfinder.controller.repositories.PharmacyRepository;
import com.pharm.pharmfinder.controller.repositories.UserRepository;
import com.pharm.pharmfinder.jwt.JwtTokenUtil;
import com.pharm.pharmfinder.model.Medicine;
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


    @GetMapping(path = "/pharmacy", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String index(HttpServletRequest request,@RequestParam String pzn,@RequestParam String latitude,@RequestParam String longitude) throws MedicineNotFoundException {
//
//        String jwt = request.getHeader("Authorization").substring(7);
//        String jwtUsername = jwtTokenUtil.getUsernameFromToken(jwt);
//        User manipulatingUser = userRepository.findByUsername(jwtUsername);

//        String username = request.getParameter("username");
//        checkAuthorization(request, username);

        //Medicine foundMedicine=null;
        ArrayList<Pharmacy> pharmacies = new ArrayList<>();
        HashMap<Pharmacy, Double> nearbyPharmacies = new HashMap<Pharmacy,Double>();
        StringBuilder result = new StringBuilder();

        System.out.println(pzn);
        Iterable<PharmacyMedicine> pharmacyMedicines = pharmacyMedicineRepository.findAll();
        for(PharmacyMedicine p : pharmacyMedicines){
            if(p.getMedicine().getPzn().equals(pzn)){
                pharmacies.add(p.getPharmacy());
               // return p.getMedicine().getPzn()+"hier";

            }
        }

        if(pharmacies.size()>0){
            nearbyPharmacies=searchPharmacies(pharmacies,Double.parseDouble(latitude),Double.parseDouble(longitude));
        }else{
//            throw new MedicineNotFoundException("Medicine was not found");
            return "warum?";
        }


//        Iterable<PharmacyMedicine> pharmacyMedicines = getPharmacy(username).getPharmacyMedicines();
//        StringBuilder result = new StringBuilder();
//        for (PharmacyMedicine pharmacyMedicine : pharmacyMedicines) {
//            Medicine medicine = pharmacyMedicine.getMedicine();
//            Pharmacy pharmacy = pharmacyMedicine.getPharmacy();
//            if (pharmacy.getPharmacyName().equals(username)){
//                result.append(medicine.toString());
//                result.append(" Amount: ").append(pharmacyMedicine.getAmount());
//                result.append("\n");
//            }
//        }
//        if (result.toString().equals(""))
//            result.append("No medicines registered");
//        return result.toString();





        for(Pharmacy p: nearbyPharmacies.keySet()){
            result.append("Pharmacy: ").append(p.getPharmacyID()).append(" Latitude: ").append(p.lat).append(" Longitude: ").append(p.lng).append(" Distance: ").append(String.valueOf(nearbyPharmacies.get(p)));
        }
        return result.toString();
    }

    private HashMap<Pharmacy,Double> searchPharmacies(ArrayList<Pharmacy> pharmacies, double lat,double lng) {

        HashMap<Pharmacy,Double> nearbyPharmacies = new HashMap<Pharmacy, Double>();
        for(Pharmacy p: pharmacies){
//            double latP=Double.parseDouble(p.getLat());
//            double lngP=Double.parseDouble(p.getLng());
//            double theta = lng - lngP;
//            double dist = Math.sin(degToRad(lat))*Math.sin(degToRad(latP)) +Math.cos(degToRad(lat))*Math.cos(degToRad(latP))
//                    +Math.cos(degToRad(theta));
//            dist = Math.acos(dist);
//            dist = radToDeg(dist);
//            dist = dist * 60; // grad in seemeile
//            dist = dist * 1852; // seemeile in meter

            //annahme dass apotheke nicht zu weit entfernt ist deshalb satz des pythagoras angewendet
            double pharLng=Double.parseDouble(p.getLng());
            double pharLat=Double.parseDouble(p.getLat());
            double latD = (lat + pharLat) / 2 * 0.01745;
            double dx = 111.3 * Math.cos(degToRad(latD)) * (lng - pharLng);
            double dy = 111.3 * (lat - pharLat);


//            double dx= 71.5* (lng-pharLng);
//            double dy = 111.3*(lat-pharLat);
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
    private double radToDeg(double x){
        return  x * 180/Math.PI;
    }

//    private Medicine checkMedicineExistence(String pzn) throws MedicineNotFoundException {
//        Iterable<Medicine> medicines = medicineRepository.findAll();
//        Medicine foundMedicine = null;
//        for(Medicine m : medicines){
//            if(m.getPzn().equals(pzn)){
//                foundMedicine=m;
//            }
//        }
//        if(foundMedicine==null) {
//            throw new MedicineNotFoundException("Medicine was not found");
//        }
//        return foundMedicine;
//    }


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
