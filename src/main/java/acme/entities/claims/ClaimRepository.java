
package acme.entities.claims;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.draftMode = false ORDER BY t.resolutionPercentage DESC")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

}
