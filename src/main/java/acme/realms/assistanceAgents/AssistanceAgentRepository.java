
package acme.realms.assistanceAgents;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentRepository extends AbstractRepository {

	@Query(value = "SELECT MONTH(c.registrationMoment) FROM Claim c GROUP BY MONTH(c.registrationMoment) ORDER BY COUNT(c) DESC LIMIT 3", nativeQuery = true)
	List<Integer> findTop3MonthsWithMostClaims();

	@Query("SELECT AVG(COUNT(t)) FROM TrackingLog t JOIN t.claim c GROUP BY c.id")
	Double findAverageTrackingLogsPerClaim();

	@Query("SELECT MAX(COUNT(t)) FROM TrackingLog t JOIN t.claim c GROUP BY c.id")
	Long findMaxTrackingLogsPerClaim();

	@Query("SELECT MIN(COUNT(t)) FROM TrackingLog t JOIN t.claim c GROUP BY c.id")
	Long findMinTrackingLogsPerClaim();

	@Query(value = "SELECT STDDEV_POP(count(t)) FROM TrackingLog t JOIN t.claim c GROUP BY c.id", nativeQuery = true)
	Double findStandardDeviationOfTrackingLogsPerClaim();

	@Query(value = "SELECT AVG(claimCount) FROM (SELECT COUNT(c) AS claimCount FROM Claim c WHERE YEAR(c.registrationMoment) = YEAR(CURRENT_DATE) AND MONTH(c.registrationMoment) = MONTH(CURRENT_DATE) - 1 GROUP BY c.assistanceAgent.id) AS claimCounts",
		nativeQuery = true)
	Double findAverageClaimsPerAssistanceAgentForLastMonth();

	@Query(value = "SELECT MAX(claimCount) FROM (SELECT COUNT(c) AS claimCount FROM Claim c WHERE YEAR(c.registrationMoment) = YEAR(CURRENT_DATE) AND MONTH(c.registrationMoment) = MONTH(CURRENT_DATE) - 1 GROUP BY c.assistanceAgent.id) AS claimCounts",
		nativeQuery = true)
	Long findMaxClaimsPerAssistanceAgentForLastMonth();

	@Query(value = "SELECT MIN(claimCount) FROM (SELECT COUNT(c) AS claimCount FROM Claim c WHERE YEAR(c.registrationMoment) = YEAR(CURRENT_DATE) AND MONTH(c.registrationMoment) = MONTH(CURRENT_DATE) - 1 GROUP BY c.assistanceAgent.id) AS claimCounts",
		nativeQuery = true)
	Long findMinClaimsPerAssistanceAgentForLastMonth();

	@Query(value = "SELECT STDDEV(claimCount) FROM (SELECT COUNT(c) AS claimCount FROM Claim c WHERE YEAR(c.registrationMoment) = YEAR(CURRENT_DATE) AND MONTH(c.registrationMoment) = MONTH(CURRENT_DATE) - 1 GROUP BY c.assistanceAgent.id) AS claimCounts",
		nativeQuery = true)
	Double findStandardDeviationClaimsPerAssistanceAgentForLastMonth();

}
