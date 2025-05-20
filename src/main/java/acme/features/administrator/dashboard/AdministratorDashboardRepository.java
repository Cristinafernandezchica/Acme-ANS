
package acme.features.administrator.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AdministratorDashboardRepository extends AbstractRepository {

	// Airports
	@Query("SELECT a.operationalScope, COUNT(a) FROM Airport a GROUP BY a.operationalScope")
	List<Object[]> countAirportsByScope();

	// Airlines
	@Query("SELECT a.type, COUNT(a) FROM Airline a GROUP BY a.type")
	List<Object[]> countAirlinesByType();

	@Query("SELECT COUNT(a) FROM Airline a WHERE a.email IS NOT NULL AND a.phoneNumber IS NOT NULL")
	Long countAirlinesWithBothContacts();

	@Query("SELECT COUNT(a) FROM Airline a")
	Long countTotalAirlines();

	// Aircrafts
	@Query("SELECT COUNT(a) FROM Aircraft a WHERE a.status = 'ACTIVE_SERVICE'")
	Long countActiveAircrafts();

	@Query("SELECT COUNT(a) FROM Aircraft a WHERE a.status = 'UNDER_MAINTENANCE'")
	Long countInactiveAircrafts();

	@Query("SELECT COUNT(a) FROM Aircraft a")
	Long countTotalAircrafts();

	// Reviews
	@Query("SELECT COUNT(r) FROM Review r WHERE r.score > 5.0")
	Long countHighScoreReviews();

	@Query("SELECT COUNT(r) FROM Review r")
	Long countTotalReviews();

	@Query("SELECT COUNT(r) FROM Review r WHERE r.postedMoment >= CURRENT_DATE - 70")
	Integer countReviewsLast10Weeks();

	@Query("SELECT COUNT(r) FROM Review r WHERE r.postedMoment >= CURRENT_DATE - 70 GROUP BY FUNCTION('WEEK', r.postedMoment)")
	List<Long> countReviewsPerWeekLast10Weeks();

}
