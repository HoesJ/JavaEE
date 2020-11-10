package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        new Main("trips").run();
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext ctx = new InitialContext();
        ReservationSessionRemote session = (ReservationSessionRemote) ctx.lookup(ReservationSessionRemote.class.getName());
        return session;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        InitialContext ctx = new InitialContext();
        ManagerSessionRemote session = (ManagerSessionRemote) ctx.lookup(ManagerSessionRemote.class.getName());
        return session;
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

    /*@Override;
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsByRenter(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }*/

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}