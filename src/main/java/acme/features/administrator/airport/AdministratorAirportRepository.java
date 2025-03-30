
package acme.features.administrator.airport;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;

@Repository
public interface AdministratorAirportRepository extends AbstractRepository {

	@Query("select a from Airport a where a.id = :id")
	Airport findAirportById(int id);

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

	@Query("SELECT (COUNT(a) + COUNT(al)) FROM Airport a, Airline al WHERE (a.iataCode = :iataCode OR al.iataCode = :iataCode) AND (a.id <> :airportId OR al.iataCode = :iataCode)")
	long countByIataCodeExcludingAirport(String iataCode, int airportId);

}
