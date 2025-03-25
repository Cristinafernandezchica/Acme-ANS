
package acme.entities.aircrafts;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AircraftRepository extends AbstractRepository {

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircraft();

}
