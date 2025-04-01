
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

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id in (select b.id from Booking b where b.customer.id = :customerId)")
	List<Passenger> findPassengerByCustomerBookings(int customerId);

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

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId and br.passenger.draftMode = true")
	Collection<Passenger> getPassengersInDraftMode(int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId and b.draftMode = true")
	Collection<Booking> findDraftBookingsByCustomerId(final int customerId);

	@Query("select b from Booking b where b.customer.id = :customerId and b.draftMode = false")
	Collection<Booking> findPublishedBookingsByCustomerId(final int customerId);
}
