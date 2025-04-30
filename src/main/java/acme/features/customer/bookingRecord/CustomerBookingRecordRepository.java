
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(@Param("bookingId") int bookingId);

	@Query("select p from Passenger p where p.id= :bookingId")
	Passenger findPassengerById(@Param("bookingId") int bookingId);

	@Query("SELECT p FROM Passenger p JOIN Booking b ON b.customer.id = p.customer.id LEFT JOIN BookingRecord br ON br.passenger.id = p.id AND br.booking.id = b.id WHERE p.customer.id = :customerId AND b.id = :bookingId AND br.id IS NULL")
	Collection<Passenger> findAvailablePassengersByBookingId(@Param("customerId") int customerId, @Param("bookingId") int bookingId);

}
