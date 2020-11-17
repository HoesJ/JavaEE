/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class Queries {
    
    public CarRentalCompany getRentalCompany(EntityManager em, String name) {
        return em.find(CarRentalCompany.class, name);
    }
    
    public List<String> getAllRentalCompanies(EntityManager em) {
        return em.createQuery(
            "SELECT c.name FROM CarRentalCompany c")
            .getResultList();
    }
    
    public List<CarType> getCarTypes(EntityManager em, String company) {
        return em.createQuery(
            "SELECT carType " + 
            "FROM CarRentalCompany company, IN(company.carTypes) carType " +
            "WHERE company.name LIKE :companyName")
            .setParameter("companyName", company)
            .getResultList();
    }
    
    public List<Integer> getCarIds(EntityManager em, String company, String type) {
        return em.createQuery(
            "SELECT car.id " + 
            "FROM CarRentalCompany company, CarType carType, IN(company.cars) car " + 
            "WHERE company.name LIKE :companyName AND carType.name LIKE :type AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getResultList();
    }
    
    public int getNumberOfReservations(EntityManager em, String company, String type, int id) {
        Long l = (Long) em.createQuery(
            "SELECT COUNT(reservation.autoId) " +
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car " +
            "WHERE company.name LIKE :companyName AND car.id = :id AND reservation.carId = :id")
            .setParameter("companyName", company).setParameter("id", id)
            .getResultList().get(0);
        return l.intValue();
        
    }
    
    public int getNumberOfReservations(EntityManager em, String company, String type) {
        Long l = (Long) em.createQuery(
            "SELECT COUNT(reservation.autoId) " + 
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car, CarType carType " +
            "WHERE company.name LIKE :companyName AND carType.name LIKE :type AND reservation.carId = car.id " +
            "AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getResultList().get(0);
        return l.intValue();
    }
    
    public List<CarType> getAvailableCarTypes(EntityManager em, Date start, Date end) {
        String overlappingReservations = 
            "SELECT DISTINCT car.autoId " +
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car " +
            "WHERE reservation.endDate >= :start AND reservation.startDate <= :end " +
                "AND reservation.rentalCompany LIKE company.name AND reservation.carId = car.id";

        return em.createQuery(
            "SELECT car.type " + 
            "FROM Car car " +
            "WHERE car.autoId NOT IN (" + overlappingReservations + ")"    
            ).setParameter("start", start)
            .setParameter("end", end)
            .getResultList();
    }
    
    public int getNumberOfReservationsByRenter(EntityManager em, String renter) {
        return (int) em.createQuery(
            "SELECT COUNT(reservation.autoId) " +
            "FROM Reservation reservation " +
            "WHERE reservation.carRenter LIKE :renterName"
        ).setParameter("renterName", renter)
        .getResultList().get(0);
    }
    
    public List<String> getBestClients(EntityManager em) {
        String reservationsPerRenter = 
            "SELECT resevation.carRenter AS carRenter, COUNT(reservation.autoId) AS nbReservations" +
            "FROM Reservation reservation " +
            "GROUP BY resevation.carRenter";

        return em.createQuery(
            "SELECT t.carRenter " +
            "FROM (" + reservationsPerRenter + ") AS t " +
            "HAVING t.nbReservations = MAX(t.nbReservations)"
        ).getResultList();
    }
    
    public CarType getMostPopularCarType(EntityManager em, String company, int year) {
        String reservationPerType =
        "SELECT reservation.carType AS carType, COUNT(reservation.autoId) AS nbReservations" +
        "FROM Reservation reservation " +
        "WHERE YEAR(reservation.startDate) = :year " +
        "GROUP BY reservation.carType";

        return (CarType) em.createQuery(
            "SELECT t.carType " +
            "FROM (" + reservationPerType + ") AS t " +
            "HAVING t.nbReservations = MAX(t.nbReservations)"
        ).setParameter("year", year)
        .getResultList().get(0); // TODO: or return list?
    }
    
    public CarType getCheapestAvailableCarType(EntityManager em, Date start, Date end, String region) {
        String overlappingReservations = 
            "SELECT DISTINCT car.autoId AS carId " +
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car " +
            "WHERE reservation.endDate >= :start AND reservation.startDate <= :end AND :region MEMBER OF company.regions " +
                "AND reservation.rentalCompany LIKE company.name AND reservation.carId = car.id";
              
        return (CarType) em.createQuery(
            "SELECT DISTINCT carType " +
            "FROM Car car, CarType carType " +
            "WHERE car.autoId NOT IN (" + overlappingReservations + ") AND car.type = carType " +
            "HAVING carType.rentalPricePerDay = MIN(carType.rentalPricePerDay)"
        ).setParameter("start", start)
        .setParameter("end", end)
        .setParameter("region", region)
        .getResultList().get(0);
    }
    
}
