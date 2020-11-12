/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class Queries {

    @PersistenceContext
    EntityManager em;
    
    public CarRentalCompany getRentalCompany(String name) {
        return em.find(CarRentalCompany.class, name);
    }
    
    public List<String> getAllRentalCompanies() {
        return em.createQuery(
            "SELECT c.name FROM CarRentalCompany c")
            .getResultList();
    }
    
    public List<String> getCarTypes(String company) {
        return em.createQuery(
            "SELECT type.name FROM CarRentalCompany company, CarType type WHERE company.name LIKE :companyName AND type IN company.carTypes")
            .setParameter("companyName", company)
            .getResultList();
    }
    
}
