
package acme.realms.customers;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.TravelClass;

@Repository
public interface CustomerRepository extends AbstractRepository {

	@Query("SELECT DISTINCT f.destinationCity FROM Booking b " + "JOIN b.flight f WHERE b.customer.id = :customerId " + "ORDER BY b.purchaseMoment DESC LIMIT 5")
	List<String> findLastFiveDestinations(@Param("customerId") int customerId);

	@Query("SELECT SUM(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - INTERVAL 1 YEAR")
	Double findMoneySpentLastYear(@Param("customerId") int customerId);

	@Query("SELECT b.travelClass, COUNT(b) FROM Booking b " + "WHERE b.customer.id = :customerId " + "GROUP BY b.travelClass")
	Map<TravelClass, Integer> findBookingCountByTravelClass(@Param("customerId") int customerId);

	@Query("SELECT COUNT(b) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - INTERVAL 5 YEAR")
	Integer findBookingCountLastFiveYears(@Param("customerId") int customerId);

	@Query("SELECT COUNT(b), AVG(b.price.amount), MIN(b.price.amount), MAX(b.price.amount), STDDEV(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - INTERVAL 5 YEAR")
	Object[] findBookingStatisticsLastFiveYears(@Param("customerId") int customerId);

	@Query("SELECT COUNT(br), AVG(br), MIN(br), MAX(br), STDDEV(br) FROM ( " + "SELECT COUNT(br.passenger.id) AS passengerCount FROM BookingRecord br " + "JOIN br.booking b WHERE b.customer.id = :customerId " + "GROUP BY b.id) AS subquery")
	Object[] findPassengerStatistics(@Param("customerId") int customerId);

	@Query("SELECT COUNT(br) FROM BookingRecord br " + "JOIN br.booking b WHERE b.customer.id = :customerId")
	Integer findPassengerCount(@Param("customerId") int customerId);
}
