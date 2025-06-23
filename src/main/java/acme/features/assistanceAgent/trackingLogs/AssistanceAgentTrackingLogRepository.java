
package acme.features.assistanceAgent.trackingLogs;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.resolutionPercentage DESC")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :trackingLogId ORDER BY t.resolutionPercentage DESC")
	Collection<TrackingLog> findTrackingLogsByClaimIdExcludingOne(int claimId, int trackingLogId);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Claim findClaimByTrackingLogId(int id);

	@Query("select tr from TrackingLog tr where tr.claim.id = :claimId AND tr.resolutionPercentage = 100.00")
	Collection<TrackingLog> findTrackingLogs100PercentageByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.id <> :trackingLogId AND t.resolutionPercentage = 100.00")
	Collection<TrackingLog> findTrackingLogs100PercentageByClaimIdExcludingOne(int claimId, int trackingLogId);

}
