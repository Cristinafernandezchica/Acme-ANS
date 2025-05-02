
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;

@Repository
public interface AdministratorAirlineRepository extends AbstractRepository {

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);

	@Query("SELECT a FROM Airline a WHERE a.iataCode = :iataCode")
	Airline findByIATACode(String iataCode);

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();

	//@Query("SELECT COUNT(a) FROM Airline a WHERE a.iataCode = :iataCode AND (:airlineId IS NULL OR a.id <> :airlineId)")
	@Query("SELECT (COUNT(a) + COUNT(al)) FROM Airport a, Airline al WHERE (a.iataCode = :iataCode OR al.iataCode = :iataCode) AND (al.id <> :airlineId OR a.iataCode = :iataCode)")
	int countByIataCodeExcludingAirline(@Param("iataCode") String iataCode, @Param("airlineId") int airlineId);

}
