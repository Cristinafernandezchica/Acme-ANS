
package acme.features.manager.legs;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.draftMode = false")
	Collection<Leg> findAllPublishedLegs();

	@Query("select l from Leg l where l.flight.manager.id = :managerId")
	Collection<Leg> findAllLegsByManagerId(int managerId);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select a from Aircraft a where a.airline.id = :airlineId")
	Collection<Aircraft> findAllAircraftsByAirlineId(int airlineId);

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findAllLegsByFlightId(int flightId);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select l.flightNumber from Leg l where l.flight.manager.airline.id = :airlineId")
	Collection<String> findAllLegsFlightNumberByAirlineId(int airlineId);

	@Query("select l from Leg l where l.flight.id = :flightId and l.draftMode = false")
	Collection<Leg> findAllPublishedLegsByFlightId(int flightId);

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

}
