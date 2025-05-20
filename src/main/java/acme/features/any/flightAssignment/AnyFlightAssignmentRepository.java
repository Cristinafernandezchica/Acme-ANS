
package acme.features.any.flightAssignment;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface AnyFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.draftMode = false")
	List<FlightAssignment> findCompletedFlightAssignmentByFlightCrewMemberId();

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select l from Leg l")
	List<Leg> findAllLegs();

	@Query("select fcm from FlightCrewMember fcm where fcm.availabilityStatus = acme.realms.flightCrewMember.AvailabilityStatus.AVAILABLE")
	Collection<FlightCrewMember> findAvailableFlightCrewMembers();
}
