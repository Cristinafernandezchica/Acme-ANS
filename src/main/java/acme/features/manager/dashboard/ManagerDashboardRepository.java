
package acme.features.manager.dashboard;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	@Query("SELECT COUNT(m) + 1 FROM Manager m WHERE m.yearsExperience > (SELECT m2.yearsExperience FROM Manager m2 WHERE m2.id = :managerId)")
	Optional<Integer> findManagerRanking(int managerId);

	@Query("SELECT 65 - YEAR(CURRENT_DATE) + YEAR(m.dateBirth) FROM Manager m WHERE m.id = :managerId")
	Optional<Integer> findYearsToRetire(int managerId);

	@Query("SELECT SUM(CASE WHEN l.status = 'ON_TIME' THEN 1 ELSE 0 END) FROM Leg l WHERE l.flight.manager.id = :managerId")
	Optional<Integer> findOnTimeLegs(int managerId);

	@Query("SELECT SUM(CASE WHEN l.status = 'DELAYED' THEN 1 ELSE 0 END) FROM Leg l WHERE l.flight.manager.id = :managerId")
	Optional<Integer> findDelayedLegs(int managerId);

	@Query("SELECT l FROM Leg l WHERE l.flight.manager.id = :managerId")
	Collection<Leg> findLegsByManagerId(int managerId);

	@Query("SELECT l.status, COUNT(l) FROM Leg l WHERE l.flight.manager.id = :managerId GROUP BY l.status")
	Optional<List<Object[]>> findLegsGroupedByStatus(int managerId);

	@Query("SELECT AVG(f.cost.amount) FROM Flight f WHERE f.manager.id = :managerId")
	Optional<Double> findAverageFlightCost(int managerId);

	@Query("SELECT MIN(f.cost.amount) FROM Flight f WHERE f.manager.id = :managerId")
	Optional<Double> findMinimumFlightCost(int managerId);

	@Query("SELECT MAX(f.cost.amount) FROM Flight f WHERE f.manager.id = :managerId")
	Optional<Double> findMaximumFlightCost(int managerId);

	@Query("SELECT STDDEV(f.cost.amount) FROM Flight f WHERE f.manager.id = :managerId")
	Optional<Double> findStandardDeviationOfFlightCost(int managerId);

}
