
package acme.entities.recommendation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface RecommendationRepository extends AbstractRepository {

	@Query("SELECT r FROM Recommendation r WHERE r.airport.iataCode = :iataCode ORDER BY r.category, r.estimatedCost.amount")
	List<Recommendation> findByAirportIataCode(@Param("iataCode") String iataCode);

	@Query("SELECT r FROM Recommendation r WHERE r.airport.city = :city AND r.airport.country = :country")
	List<Recommendation> findByCityAndCountry(@Param("city") String city, @Param("country") String country);

	Optional<Recommendation> findByExternalIdAndProviderName(String externalId, String providerName);
}
