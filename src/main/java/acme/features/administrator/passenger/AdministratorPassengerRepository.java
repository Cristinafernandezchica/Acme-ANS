
package acme.features.administrator.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@Repository
public interface AdministratorPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(@Param("passengerId") int passengerId);

	@Query("select distinct br.passenger from BookingRecord br where br.passenger is not null and br.passenger.draftMode = false and br.booking.draftMode = false")
	Collection<Passenger> findPassengersWithBookingRecordAssigned();

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(@Param("bookingId") int bookingId);

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(@Param("bookingId") int bookingId);

}
