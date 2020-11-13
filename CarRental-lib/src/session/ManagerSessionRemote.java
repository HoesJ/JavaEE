package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public void addRentalCompany(String file);
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
    public int getNumberOfReservationsByRenter(String name);
    
    public Set<String> getBestClients();
    
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year);
    
    public int getNumberOfReservationsByCarType(String carRentalName, String carType);
    
}