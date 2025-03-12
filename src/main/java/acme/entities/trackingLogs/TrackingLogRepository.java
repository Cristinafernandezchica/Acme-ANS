
package acme.entities.trackingLogs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId")
	List<TrackingLog> findTrackingLogsByClaimId(@Param("claimId") int claimId);

	@Query("SELECT t.claim.accepted FROM TrackingLog t WHERE t.id = :trackingLogId")
	Boolean findClaimIndicatorByTrackingLogId(@Param("trackingLogId") int trackingLogId);

}
