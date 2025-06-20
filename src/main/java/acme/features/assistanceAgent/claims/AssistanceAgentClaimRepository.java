
package acme.features.assistanceAgent.claims;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select a from AssistanceAgent a where a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId AND NOT EXISTS (SELECT tr FROM TrackingLog tr WHERE tr.claim = c AND tr.resolutionPercentage = 100.00 AND tr.draftMode = false)")
	Collection<Claim> findAllUndergoingClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("select distinct tr.claim from TrackingLog tr WHERE tr.claim.assistanceAgent.id = :assistanceAgentId AND tr.resolutionPercentage = 100.00 AND tr.draftMode = false")
	Collection<Claim> findAllCompletedClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.scheduledArrival <= :now AND l.aircraft.airline.id = :agentAirlineId")
	Collection<Leg> findAllPublishedLegs(Date now, int agentAirlineId);

}
