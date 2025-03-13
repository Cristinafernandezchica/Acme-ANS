
package acme.forms.customerDashboard;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.TravelClass;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT DISTINCT l.arrivalAirport.city FROM Booking b " + "JOIN b.flight f JOIN Leg l ON l.flight.id = f.id " + "WHERE b.customer.id = :customerId " + "ORDER BY b.purchaseMoment DESC")
	List<String> findLastFiveDestinations(@Param("customerId") int customerId);

	@Query("SELECT SUM(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Double findMoneySpentLastYear(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT b.travelClass, COUNT(b) FROM Booking b " + "WHERE b.customer.id = :customerId " + "GROUP BY b.travelClass")
	Map<TravelClass, Integer> findBookingCountByTravelClass(@Param("customerId") int customerId);

	@Query("SELECT COUNT(b) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Integer findBookingCountLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT COUNT(b), AVG(b.price.amount), MIN(b.price.amount), MAX(b.price.amount), STDDEV(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Object[] findBookingStatisticsLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT COUNT(br), AVG(COUNT(br.passenger.id)), MIN(COUNT(br.passenger.id)), MAX(COUNT(br.passenger.id)), STDDEV(COUNT(br.passenger.id)) " + "FROM BookingRecord br JOIN br.booking b " + "WHERE b.customer.id = :customerId " + "GROUP BY b.id")
	Object[] findPassengerStatistics(@Param("customerId") int customerId);

	@Query("SELECT COUNT(br) FROM BookingRecord br " + "JOIN br.booking b WHERE b.customer.id = :customerId")
	Integer findPassengerCount(@Param("customerId") int customerId);

	@Query("SELECT AVG(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Double findBookingAverageCostLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT MIN(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Double findBookingMinCostLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT MAX(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Double findBookingMaxCostLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);

	@Query("SELECT STDDEV(b.price.amount) FROM Booking b " + "WHERE b.customer.id = :customerId " + "AND b.purchaseMoment >= CURRENT_DATE - :years * 365")
	Double findBookingStdDevCostLastFiveYears(@Param("customerId") int customerId, @Param("years") int years);
}
