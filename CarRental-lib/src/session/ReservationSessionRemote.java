package session;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.Reservation;

@Remote
public interface ReservationSessionRemote {
    
    public HashSet<Quote> getCurrentQuotes();

    Set<String> getAllRentalCompanies();
    
    Set<CarType> getAvailableCarTypes(Date start, Date end);
    
    void createQuote(ReservationConstraints constraints, String client) throws ReservationException;
    
    List<Reservation> confirmQuotes() throws ReservationException;
    
}
