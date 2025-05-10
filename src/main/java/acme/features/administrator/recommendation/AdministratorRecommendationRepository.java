
package acme.features.administrator.recommendation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.recommendation.Recommendation;

@Repository
public interface AdministratorRecommendationRepository extends AbstractRepository {

	@Query("select r from Recommendation r where r.airport.city = :city")
	List<Recommendation> findByCity(@Param("city") String city);

	@Query("select distinct a.city from Airport a order by a.city")
	List<String> findDistinctCities();

	@Query("select r from Recommendation r where r.externalId = :externalId and r.providerName = :provider")
	Optional<Recommendation> findByExternalIdAndProvider(@Param("externalId") String externalId, @Param("provider") String provider);
}
