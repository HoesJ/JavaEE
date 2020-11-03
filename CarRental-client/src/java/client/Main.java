package client;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestAgency<ReservationSessionRemote, ManagerSessionRemote> {
    
    @EJB
    static ReservationSessionRemote reservationSession;
    
    @EJB
    static ManagerSessionRemote managerSession;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("found rental companies: "+reservationSession.getAllRentalCompanies());
        
        Main client = new Main("simpleTrips");
        client.run();
    }

    public Main(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        return reservationSession;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        return managerSession;
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        session.getAvailableCarTypes(start, end);
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
        session.createQuote(constraints, name);
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsByRenter(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }
}
