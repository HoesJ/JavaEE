package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Queries;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@PermitAll
@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    private Queries queries = new Queries();
    
    @Resource 
    private EJBContext context;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<>(queries.getAllRentalCompanies());
    }
    
    @Override
    public void getAvailableCarTypes(Date start, Date end) {
        List<CarType> res = queries.getAvailableCarTypes(start, end);
        for (Object type : res)
            System.out.println(type);
    }

    @Override
    public Quote createQuote(String renter, ReservationConstraints constraints) throws ReservationException {
        //try {
	for (String company : queries.getAllRentalCompanies()) {
            try {
                System.out.println("HALOOO");
                return queries.getRentalCompany(company).createQuote(constraints, renter);
            } catch (ReservationException exception) {
                System.out.println(exception.getMessage());
                continue;
            }
            // For if there is no car with the given car type in the current car rental company.
            catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
                continue;   
            }
        }
		
	throw new ReservationException("<" + renter + "> No cars available to satisfy the given constraints.");
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        try {
            List<Reservation> done = new LinkedList<>();
            for (Quote quote : quotes) {
                done.add(queries.getRentalCompany(quote.getRentalCompany()).confirmQuote(quote));
            }
            
            quotes.clear();
            return done;
        } catch (Exception e) {
            context.setRollbackOnly();
            throw new ReservationException(e);
        }
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        return renter;
    }
    
    @Override
    public String getCheapestCarType(Date start, Date end, String region) {
        return queries.getCheapestAvailableCarType(start, end, region).getName();
    }
    
}