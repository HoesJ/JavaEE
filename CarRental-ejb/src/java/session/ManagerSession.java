package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Queries;

@DeclareRoles({"Manager", "User"})

@RolesAllowed({"Manager"})
@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    private Queries queries = new Queries();
    
    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<>(queries.getCarTypes(em, company));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public Set<Integer> getCarIds(String company, String type) {
        try {
            return new HashSet<>(queries.getCarIds(em, company, type));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return queries.getNumberOfReservations(em, company, type, id);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public int getNumberOfReservations(String company, String type) {
        try {
            return queries.getNumberOfReservations(em, company, type);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public int getNumberOfReservationsByRenter(String name) {
        try {
            return queries.getNumberOfReservationsByRenter(em, name);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public Set<String> getBestClients() {
        try {
            return new HashSet<>(queries.getBestClients(em));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
        try {
            return queries.getMostPopularCarType(em, carRentalCompanyName, year);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    // TODO
    public int getNumberOfReservationsByCarType(String carRentalName, String carType) {
        try {
            return queries.getNumberOfReservations(em, carRentalName, carType);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    @Override
    public void addRentalCompany(String file) {
        try {
            CrcData data = loadData(file);
            CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
            em.persist(company);
            Logger.getLogger(ManagerSession.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, file});
        } catch (NumberFormatException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loading Car rental companies
     */
    // TODO
    public static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(ManagerSession.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(nextuid++, type));
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
            public List<Car> cars = new LinkedList<>();
            public String name;
            public List<String> regions =  new LinkedList<>();
    }

}
