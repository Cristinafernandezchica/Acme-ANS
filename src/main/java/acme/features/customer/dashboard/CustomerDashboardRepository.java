
package acme.features.customer.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT DISTINCT l.arrivalAirport.city FROM Booking b JOIN b.flight f JOIN Leg l ON l.flight.id = f.id WHERE b.customer.id = :customerId ORDER BY b.purchaseMoment DESC")
	List<String> findLastFiveDestinations(@Param("customerId") int customerId);

	@Query("SELECT b.price.currency, SUM(b.price.amount) FROM Booking b WHERE b.customer.id = :customerId AND b.purchaseMoment >= CURRENT_DATE - 365 AND b.draftMode = false GROUP BY b.price.currency")
	List<Object[]> findMoneySpentLastYear(@Param("customerId") int customerId);

	@Query("SELECT b.travelClass, COUNT(b) FROM Booking b WHERE b.customer.id = :customerId GROUP BY b.travelClass")
	List<Object[]> findBookingCountByTravelClass(@Param("customerId") int customerId);

	@Query("SELECT COUNT(b) FROM Booking b WHERE b.customer.id = :customerId AND b.purchaseMoment >= CURRENT_DATE - 365 * 5 AND b.draftMode = false")
	Integer findBookingCountLastFiveYears(@Param("customerId") int customerId);

	@Query("SELECT b.price.currency, AVG(b.price.amount), MIN(b.price.amount), MAX(b.price.amount), STDDEV(b.price.amount) FROM Booking b WHERE b.customer.id = :customerId AND b.purchaseMoment >= CURRENT_DATE - 365 * 5 GROUP BY b.price.currency")
	List<Object[]> findBookingStatisticsLastFiveYears(@Param("customerId") int customerId);

	@Query("SELECT COUNT(br) FROM BookingRecord br JOIN br.booking b WHERE b.customer.id = :customerId")
	Integer findPassengerCount(@Param("customerId") int customerId);

	@Query("SELECT COUNT(br) FROM BookingRecord br JOIN br.booking b WHERE b.customer.id = :customerId GROUP BY b.id")
	List<Long> findPassengerCountsPerBooking(@Param("customerId") int customerId);

}
