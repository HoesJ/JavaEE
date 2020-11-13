package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateful;
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
    
    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<>(queries.getAllRentalCompanies());
    }
    
    @Override
    public void getAvailableCarTypes(Date start, Date end) {
        for (CarType type : queries.getAvailableCarTypes(start, end))
            System.out.println(type);
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = queries.getRentalCompany(company).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                done.add(queries.getRentalCompany(quote.getRentalCompany()).confirmQuote(quote));
            }
            
            quotes.clear();
            return done;
        } catch (Exception e) {
            for (Reservation r : done)
                queries.getRentalCompany(r.getRentalCompany()).cancelReservation(r);
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