package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarType;
import rental.Queries;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@PermitAll
@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    private Queries queries = new Queries();
    
    @Resource
    private javax.ejb.SessionContext context;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<>(queries.getAllRentalCompanies(em));
    }
    
    @Override
    public void getAvailableCarTypes(Date start, Date end) {
        List<CarType> res = queries.getAvailableCarTypes(em, start, end);
        for (Object type : res)
            System.out.println(type);
    }

    @Override
    public void createQuote(String renter, ReservationConstraints constraints) throws ReservationException {
	for (String company : queries.getAllRentalCompanies(em)) {
            try {
                quotes.add(queries.getRentalCompany(em, company).createQuote(constraints, renter));
                return;
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
                done.add(queries.getRentalCompany(em, quote.getRentalCompany()).confirmQuote(quote));
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
        return queries.getCheapestAvailableCarType(em, start, end, region).getName();
    }
    
}