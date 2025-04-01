
package acme.features.assistanceAgent.claims;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId AND c.accepted != null")
	Collection<Claim> findAllCompletedClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId AND c.accepted = null")
	Collection<Claim> findAllUndergoingClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false")
	Collection<Leg> findAllPublishedLegs();

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.lastUpdateMoment DESC")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

}
