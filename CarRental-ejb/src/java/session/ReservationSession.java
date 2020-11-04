package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.Reservation;

@Stateful
public class ReservationSession implements ReservationSessionRemote {

    private Set<Quote> quotes = new HashSet<>();

    @Override
    public HashSet<Quote> getCurrentQuotes() {
        return new HashSet<Quote>(quotes);
    }

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> types = new HashSet<>();
        for (String companyName : getAllRentalCompanies()) {
            CarRentalCompany company = RentalStore.getRental(companyName);
            types.addAll(company.getAvailableCarTypes(start, end));
        }

        return types;
    }

    @Override
    public void createQuote(ReservationConstraints constraints, String client) throws ReservationException {
        for (String companyName : getAllRentalCompanies()) {
            try {
                CarRentalCompany company = RentalStore.getRental(companyName);
                quotes.add(company.createQuote(constraints, client));
                return;
            } catch (ReservationException exception) {
                continue;
            }
        }

        throw new ReservationException("<" + RentalStore.getName() + "> No cars available to satisfy the given constraints.");
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> reservations = new ArrayList<>();
        for (Quote quote : quotes) {
            try {
                Reservation reservation = RentalStore.getRental(quote.getRentalCompany()).confirmQuote(quote);
                reservations.add(reservation);
            } catch (ReservationException exception) {
                for (Reservation reservation : reservations) {
                    RentalStore.getRental(reservation.getRentalCompany()).cancelReservation(reservation);
                }
                throw exception;
            }
        }

        return reservations;
    }
    
    
}
