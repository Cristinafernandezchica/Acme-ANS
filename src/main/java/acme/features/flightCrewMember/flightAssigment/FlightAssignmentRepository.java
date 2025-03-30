
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.flightCrewMemberAssigned.id = :id ")
	Collection<FlightAssignment> findFlightAssignmentsByFlightCrewMemberId(int id);

	//@Query("select fa from FlightAssignment fa where fa.legRelated.status = 'LANDED' or fa.legRelated.status = 'CANCELLED'")
	@Query("select fa from FlightAssignment fa where fa.legRelated.scheduledArrival<:currentDate")
	Collection<FlightAssignment> findCompletedFlightAssignmentsByFlightCrewMemberId(Date currentDate);

	//@Query("select fa from FlightAssignment fa where fa.legRelated.scheduledDeparture >= :currentDate and fa.legRelated.status != 'LANDED' and fa.legRelated.status != 'CANCELLED'")
	@Query("select fa from FlightAssignment fa where fa.legRelated.scheduledDeparture>:currentDate")
	Collection<FlightAssignment> findPlannedFlightAssignmentsByFlightCrewMemberId(Date currentDate);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.availabilityStatus = 'AVAILABLE'")
	Collection<FlightCrewMember> findAvailableFlightCrewMembers();

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlighCrewMemberById(int id);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l where l.scheduledDeparture >= :currentDate")
	Collection<Leg> findNotPastLegsById(Date currentDate);

}
