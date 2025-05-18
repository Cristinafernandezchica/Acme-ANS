
package acme.features.customer.booking;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.customer.id = :customerId")
	List<Booking> findBookingsByCustomerId(int customerId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> findLegsByFlightId(@Param("flightId") int flightId);

	@Query("select f from Flight f where f.id = :flightId")
	Flight findFlightById(int flightId);

	@Query("select b.locatorCode from Booking b")
	Collection<String> findAllLocatorCodes();

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);
}
