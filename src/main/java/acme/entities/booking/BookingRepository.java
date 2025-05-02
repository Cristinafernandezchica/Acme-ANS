
package acme.entities.booking;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.passenger.Passenger;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("SELECT DISTINCT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	public List<Passenger> findPassengersByBookingId(@Param("bookingId") int bookingId);

	@Query("SELECT b FROM Booking b WHERE b.locatorCode = :locatorCode")
	public Booking findBookingByLocatorCode(@Param("locatorCode") String locatorCode);
}
