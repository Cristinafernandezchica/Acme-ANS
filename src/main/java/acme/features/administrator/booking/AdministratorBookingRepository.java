
package acme.features.administrator.booking;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AdministratorBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.draftMode = false")
	Collection<Booking> findAllBookingsPublished();

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> findLegsByFlightId(@Param("flightId") int flightId);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

}
