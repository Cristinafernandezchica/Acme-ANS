
package acme.entities.trackingLogs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("select t.claim.accepted from TrackingLog t where t.id = :trackingLogId")
	Boolean findClaimIndicator(int trackingLogId);

}
