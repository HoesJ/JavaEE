package rental;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rental.CarType;
import rental.Reservation;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-11-17T14:33:19")
@StaticMetamodel(Car.class)
public class Car_ { 

    public static volatile SetAttribute<Car, Reservation> reservations;
    public static volatile SingularAttribute<Car, Long> autoId;
    public static volatile SingularAttribute<Car, Integer> id;
    public static volatile SingularAttribute<Car, CarType> type;

}