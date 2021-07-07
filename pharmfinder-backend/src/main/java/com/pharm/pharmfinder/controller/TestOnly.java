package com.pharm.pharmfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

@Controller
@RequestMapping(path="/test_only")
public class TestOnly{

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    public EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;


    @GetMapping(path = "/deleteAll")
    public @ResponseBody
    String deleteAll() {

        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(transactionStatus -> {
            try {

                Query q1 = em.createQuery("DELETE FROM PharmFinderUser");
                Query q2 = em.createQuery("DELETE FROM PharmFinderAddress");
//                Query q3 = em.createQuery("DELETE FROM PharmFinderMedicine ");
//                Query q4 = em.createQuery("DELETE FROM PharmFinderPharmacy ");
//                Query q5 = em.createQuery("DELETE FROM PharmFinderPharmacyMedicine ");

                q1.executeUpdate();
                q2.executeUpdate();
//                q3.executeUpdate();
//                q4.executeUpdate();
//                q5.executeUpdate();
                transactionStatus.flush();

            } catch (SecurityException | IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        });
        return "all deleted";
    }
}