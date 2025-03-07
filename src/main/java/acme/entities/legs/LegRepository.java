
package acme.entities.legs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	List<Leg> findLegsByFlighId(@Param("flightId") int flighId);

}
