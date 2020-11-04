/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import rental.CarRentalCompany;
import rental.RentalStore;

@DeclareRoles({"Manager", "User"})

@RolesAllowed({"Manager"})
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    /**
     * Get the number of reservations for a specific company and a car type.
     */
    @Override
    public int getNumberOfReservationsForCarType(String company, String type) {
        return RentalStore.getRental(company).getNumberOfReservationsForCarType(type);
    }

    /**
     * Get a map that gives the number of reservations per renter.
     */
    private Map<String, Integer> getNumberOfReservationsByRenter() {
        Map<String, Integer> numResByRenter = new HashMap<>();
        for (CarRentalCompany company : RentalStore.getRentals().values()) {
            Map<String, Integer> companyNumResByRenter = company.getNumberOfReservationsByRenter();
            for (Map.Entry<String, Integer> entry : companyNumResByRenter.entrySet()) {
                numResByRenter.put(entry.getKey(), numResByRenter.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        return numResByRenter;
    }

    /**
     * Get the number of reservations for a specific renter.
     */
    @Override
    public int getNumberOfReservationsByRenter(String name) {
        return getNumberOfReservationsByRenter().getOrDefault(name, 0);
    }
}
