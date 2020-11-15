/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Queries {
    
    EntityManager em;
    
    public Queries() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CarRental-ejbPU");
        this.em = emf.createEntityManager();
    }
    
    public CarRentalCompany getRentalCompany(String name) {
        return em.find(CarRentalCompany.class, name);
    }
    
    public List<String> getAllRentalCompanies() {
        return em.createQuery(
            "SELECT c.name FROM CarRentalCompany c")
            .getResultList();
    }
    
    public List<CarType> getCarTypes(String company) {
        return em.createQuery(
            "SELECT carType " + 
            "FROM CarRentalCompany company, IN(company.carTypes) carType " +
            "WHERE company.name LIKE :companyName")
            .setParameter("companyName", company)
            .getResultList();
    }
    
    public List<Integer> getCarIds(String company, String type) {
        return em.createQuery(
            "SELECT car.id " + 
            "FROM CarRentalCompany company, CarType carType, IN(company.cars) car " + 
            "WHERE company.name LIKE :companyName AND carType.name LIKE :type AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getResultList();
    }
    
    public int getNumberOfReservations(String company, String type, int id) {
        return em.createQuery(
            "SELECT COUNT(reservation.autoId) " +
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car " +
            "WHERE company.name LIKE :companyName AND car.id = :id AND reservation.carId = :id")
            .setParameter("companyName", company).setParameter("id", id)
            .getFirstResult();
    }
    
    public int getNumberOfReservations(String company, String type) {
        return em.createQuery(
            "SELECT COUNT(reservation.autoId) " + 
            "FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car, CarType carType " +
            "WHERE company.name LIKE :companyName AND carType.name LIKE :type AND reservation.carId = car.id " +
            "AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getFirstResult();
    }
    
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        String overlappingReservations = 
            "SELECT DISTINCT car.autoID " +
            "FROM Reservation reservation, Company company, IN(company.cars) car " +
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
    
    public int getNumberOfReservationsByRenter(String renter) {
        return em.createQuery(
            "SELECT COUNT(reservation.id) " +
            "FROM Reservation reservation " +
            "WHERE reservation.carRenter LIKE :renterName"
        ).setParameter("renterName", renter)
        .getFirstResult();
    }
    
    public List<String> getBestClients() {
        String reservationsPerRenter = 
            "SELECT resevation.carRenter AS carRenter, COUNT(reservation.autoId) AS nbReservations" +
            "FROM Reservation reservation " +
            "GROUP BY resevation.carRenter";

        return em.createQuery(
            "SELECT t.carRenter" +
            "FROM (" + reservationsPerRenter + ") AS t " +
            "WHERE t.nbReservations = MAX(t.nbReservations)"
        ).getResultList();
    }
    
    public CarType getMostPopularCarType(String company, int year) {
        String reservationPerType =
        "SELECT reservation.carType AS carType, COUNT(reservation.autoId) AS nbReservations" +
        "FROM Reservation reservation " +
        "WHERE YEAR(reservation.startDate) = :year " +
        "GROUP BY reservation.carType";

        /*return em.createQuery(
            "SELECT t.carType " +
            "FROM (" + reservationPerType + ") AS t " +
            "WHERE t.nbReservations = MAX(t.nbReservations)"
        ).setParameter("year", year)
        .getFirstResult(); // TODO: or return list?*/
        return null;
    }
    
    public CarType getCheapestAvailableCarType(Date start, Date end, String region) {
        String overlappingReservations = 
            "SELECT DISTINCT car.autoID AS carId" +
            "FROM Reservation reservation, Company company, IN(company.cars) car " +
            "WHERE reservation.endDate >= :start AND reservation.startDate <= :end AND :region MEMBER OF company.regions " +
                "AND reservation.rentalCompany LIKE company.name AND reservation.carId = car.id";
            
        String pricePerType = 
            "SELECT DISTINCT carType as carType, carType.rentalPricePerDay as price " +
            "FROM Car car, CarType carType " +
            "WHERE car.autoID NOT IN (" + overlappingReservations + ") AND car.type = carType";
            
        /*return em.createQuery(
            "SELECT p.carType " + 
            "FROM (" + pricePerType + ") AS p " +
            "WHERE p.price = MIN(p.price)"
        ).setParameter("start", start)
        .setParameter("end", end)
        .setParameter("region", region)
        .getFirstResult();*/
        return null;
    }
    
}