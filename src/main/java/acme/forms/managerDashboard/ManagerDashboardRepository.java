
package acme.forms.managerDashboard;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	@Query("SELECT COUNT(m) + 1 FROM Manager m WHERE m.yearsExperience > (SELECT m2.yearsExperience FROM Manager m2 WHERE m2.id = :managerId)")
	Integer findManagerRanking(@Param("managerId") int managerId);

	@Query("SELECT 65 - YEAR(CURRENT_DATE) + YEAR(m.dateOfBirth) FROM Manager m WHERE m.id = :managerId")
	Integer findYearsToRetire(@Param("managerId") int managerId);

	@Query("SELECT SUM(CASE WHEN l.status = 'ON_TIME' THEN 1 ELSE 0 END) AS onTimeLegs, SUM(CASE WHEN l.status = 'DELAYED' THEN 1 ELSE 0 END) AS delayedLegs FROM Leg l WHERE l.flight.manager.id = :managerId")
	Object[] findLegsOnTimeAndDelayed(@Param("managerId") int managerId);

	@Query("SELECT a.name, COUNT(l) FROM Leg l JOIN l.departureAirport a WHERE l.flight.manager.id = :managerId GROUP BY a.name ORDER BY COUNT(l) DESC")
	List<Object[]> findPopularDepartureAirports(@Param("managerId") int managerId);

	@Query("SELECT a.name, COUNT(l) FROM Leg l JOIN l.arrivalAirport a WHERE l.flight.manager.id = :managerId GROUP BY a.name ORDER BY COUNT(l) DESC")
	List<Object[]> findPopularArrivalAirports(@Param("managerId") int managerId);

	@Query("SELECT l.status, COUNT(l) FROM Leg l WHERE l.flight.manager.id = :managerId GROUP BY l.status")
	List<Object[]> findLegsGroupedByStatus(@Param("managerId") int managerId);

	@Query("SELECT AVG(f.cost) FROM Flight f WHERE f.manager.id = :managerId")
	Double findAverageFlightCost(@Param("managerId") int managerId);

	@Query("SELECT MIN(f.cost) FROM Flight f WHERE f.manager.id = :managerId")
	Money findMinimumFlightCost(@Param("managerId") int managerId);

	@Query("SELECT MAX(f.cost) FROM Flight f WHERE f.manager.id = :managerId")
	Money findMaximumFlightCost(@Param("managerId") int managerId);

	@Query("SELECT STDDEV(f.cost) FROM Flight f WHERE f.manager.id = :managerId")
	Double findStandardDeviationOfFlightCost(@Param("managerId") int managerId);

}
