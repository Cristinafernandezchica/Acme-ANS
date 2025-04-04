
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.bookingRecord.BookingRecord;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select br from BookingRecord br")
	Collection<BookingRecord> findAllBookingRecords();

	@Query("select br from BookingRecord br where br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(@Param("bookingRecordId") int bookingRecordId);

	@Query("select br from BookingRecord br where br.booking.customer.id = :customerId")
	Collection<BookingRecord> findBookingRecordByCustomerId(@Param("customerId") int customerId);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(@Param("bookingId") int bookingId);

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(@Param("bookingId") int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(@Param("customerId") int customerId);

	@Query("select p from Passenger p where p.id= :bookingId")
	Passenger findPassengerById(@Param("bookingId") int bookingId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(@Param("customerId") int customerId);

	@Query("SELECT p FROM Passenger p JOIN Booking b ON b.customer.id = p.customer.id LEFT JOIN BookingRecord br ON br.passenger.id = p.id AND br.booking.id = b.id WHERE p.customer.id = :customerId AND b.id = :bookingId AND br.id IS NULL")
	Collection<Passenger> findAvailablePassengersByBookingId(@Param("customerId") int customerId, @Param("bookingId") int bookingId);

}
