
package acme.features.assistanceAgent.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;

@Repository
public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findAllClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("SELECT FUNCTION('MONTH', c.registrationMoment) AS month, COUNT(c) AS count FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId GROUP BY FUNCTION('MONTH', c.registrationMoment) ORDER BY COUNT(c) DESC")
	List<Object[]> findTopMonthsByClaimCount(int assistanceAgentId);

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.assistanceAgent.id = :assistanceAgentId GROUP BY t.claim.id")
	List<Long> findTrackingLogCountsByAssistanceAgent(int assistanceAgentId);

	@Query("SELECT COUNT(c) FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId AND FUNCTION('MONTH', c.registrationMoment) = :targetMonth AND FUNCTION('YEAR', c.registrationMoment)  = :targetYear GROUP BY FUNCTION('DAY', c.registrationMoment)")
	List<Long> findDailyClaimCountsByMonth(int assistanceAgentId, int targetMonth, int targetYear);

}
