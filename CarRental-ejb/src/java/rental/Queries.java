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
            "SELECT carType FROM CarRentalCompany company, IN(company.carTypes) carType WHERE company.name LIKE :companyName")
            .setParameter("companyName", company)
            .getResultList();
    }
    
    public List<Integer> getCarIds(String company, String type) {
        return em.createQuery(
            "SELECT car.id FROM CarRentalCompany company, CarType carType, IN(company.cars) car "
          + "WHERE company.name LIKE :companyName AND carType.name LIKE :type AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getResultList();
    }
    
    public int getNumberOfReservations(String company, String type, int id) {
        return em.createQuery(
            "SELECT COUNT(reservation.autoId) FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car "
          + "WHERE company.name LIKE :companyName AND car.id = :id AND reservation.carId = :id")
            .setParameter("companyName", company).setParameter("id", id)
            .getFirstResult();
    }
    
    public int getNumberOfReservations(String company, String type) {
        return em.createQuery(
            "SELECT COUNT(reservation.autoId) FROM Reservation reservation, CarRentalCompany company, IN(company.cars) car, "
          + "CarType carType WHERE company.name LIKE :companyName AND carType.name LIKE :type AND reservation.carId = car.id "
          + "AND car.type = carType")
            .setParameter("companyName", company).setParameter("type", type)
            .getFirstResult();
    }
    
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        // TODO: afmaken
        return em.createQuery(
            "SELECT DISTINCT car.type FROM (Car car OUTER JOIN Reservation reservation ON car.id = reservation.carId) AS t "
          + "WHERE ISNULL(t.startDate) OR ")
            .setParameter("start", start)
            .setParameter("end", end)
            .getResultList();
    }
    
    public int getNumberOfReservationsByRenter(String renter) {
        // TODO: afmaken
        return 0;
    }
    
    public List<String> getBestClients() {
        // TODO: afmaken
        return null;
    }
    
    public CarType getMostPopularCarType(String company, int year) {
        // TODO: afmaken
        return null;
    }
    
    public CarType getCheapestAvailableCarType(Date start, Date end, String region) {
        // TODO: afmaken
        return null;
    }
    
}