
package acme.features.manager.flights;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.manager.id = :managerId")
	Collection<Flight> findAllFlightsByManagerId(int managerId);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select a from Airline a where a.id = :airlineId")
	Airline findAirlineById(int airlineId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(@Param("flightId") int flightId);

}
