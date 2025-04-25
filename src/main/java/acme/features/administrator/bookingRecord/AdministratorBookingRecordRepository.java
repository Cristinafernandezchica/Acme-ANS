
package acme.features.administrator.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.bookingRecord.BookingRecord;
import acme.entities.passenger.Passenger;

@Repository
public interface AdministratorBookingRecordRepository extends AbstractRepository {

	@Query("select br from BookingRecord br")
	Collection<BookingRecord> findAllBookingRecords();

	@Query("select br from BookingRecord br where br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(@Param("bookingRecordId") int bookingRecordId);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(@Param("bookingId") int bookingId);

	@Query("select b from Booking b")
	Collection<Booking> findAllBookings();

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

}
