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
    
    public List<CarRentalCompany> getAllRentalCompaniesObject(EntityManager em) {
        return em.createQuery(
            "SELECT c FROM CarRentalCompany c")
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
        Long l = (Long) em.createQuery(
            "SELECT COUNT(reservation.autoId) " +
            "FROM Reservation reservation " +
            "WHERE reservation.carRenter LIKE :renterName"
        ).setParameter("renterName", renter)
        .getResultList().get(0);
        return l.intValue();
    }
    
    public List<String> getBestClients(EntityManager em) {
        /*  In normal SQL you could easily do this in a single query but due to 
            the many limitations of JPQL (like no subqueries in FROM or no TOP
            command) we will do it in two */
        long maxReservations = (Long) em.createQuery(
            "SELECT COUNT(reservation.autoId) " +
            "FROM Reservation reservation " +
            "GROUP BY reservation.carRenter " +
            "ORDER BY COUNT(reservation.autoId) DESC"
        ).setMaxResults(1)
        .getResultList().get(0);
        
        return em.createQuery(
            "SELECT reservation.carRenter " + 
            "FROM Reservation reservation " +
            "GROUP BY reservation.carRenter " +
            "HAVING COUNT(reservation.autoId) = :maxRes"
        ).setParameter("maxRes", maxReservations)
        .getResultList();
    }
    
    public CarType getMostPopularCarType(EntityManager em, String company, int year) {
        return (CarType) em.createQuery(
            "SELECT ct " + //, COUNT(reservation.autoId) AS nbReservations " +
            "FROM Reservation reservation, CarType ct " +
            "WHERE EXTRACT(YEAR FROM reservation.startDate) = :year " + 
                    "AND reservation.rentalCompany LIKE :company " + 
                    "AND ct.name LIKE reservation.carType " +
            "GROUP BY ct " + 
            "ORDER BY COUNT(reservation.autoId) DESC"
        ).setParameter("year", year)
        .setParameter("company", company)
        .setMaxResults(1)
        .getResultList().get(0);
    }
    
    public CarType getCheapestAvailableCarType(EntityManager em, Date start, Date end, String region) {        
        String overlappingReservations = 
            "SELECT DISTINCT car.autoId " +
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car " +
            "WHERE reservation.endDate >= :start AND reservation.startDate <= :end " +
                "AND reservation.rentalCompany LIKE company.name AND reservation.carId = car.id";
        
        String cheapestPrice =
            "SELECT MIN(car.type.rentalPricePerDay) " +
            "FROM CarRentalCompany company, In(company.cars) car " +
            "WHERE car.autoId NOT IN (" + overlappingReservations + ") AND :region MEMBER OF company.regions ";
              
        return (CarType) em.createQuery(
            "SELECT DISTINCT car.type " +
            "FROM CarRentalCompany company, In(company.cars) car " +
            "WHERE car.autoId NOT IN (" + overlappingReservations + ") AND :region MEMBER OF company.regions " +
            "AND car.type.rentalPricePerDay IN (" + cheapestPrice + ")"
        ).setParameter("start", start)
        .setParameter("end", end)
        .setParameter("region", region)
        .getResultList().get(0);
    }
    
}
