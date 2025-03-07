
package acme.entities.legs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT * FROM Leg WHERE flight_id = :flightId")
	List<Leg> findLegsByFlighId(int flighId);

}
