
package acme.entities.recommendation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface RecommendationRepository extends AbstractRepository {

	@Query("select r from Recommendation r where r.externalId = :externalId and r.providerName = :provider")
	Optional<Recommendation> findByExternalIdAndProvider(@Param("externalId") String externalId, @Param("provider") String provider);
}
