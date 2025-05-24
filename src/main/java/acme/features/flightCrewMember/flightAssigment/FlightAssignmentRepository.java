
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa")
	List<FlightAssignment> findAllFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.flightCrewMemberAssigned.id = :id and fa.draftMode = false and (fa.legRelated.status = acme.entities.legs.LegStatus.LANDED or fa.legRelated.status = acme.entities.legs.LegStatus.CANCELLED) ")
	Collection<FlightAssignment> findCompletedFlightAssignmentByFlightCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMemberAssigned.id = :id and (fa.legRelated.status = acme.entities.legs.LegStatus.DELAYED or fa.legRelated.status = acme.entities.legs.LegStatus.ON_TIME)")
	Collection<FlightAssignment> findPlannedFlightAssignmentByFlightCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlighCrewMemberById(int id);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l")
	List<Leg> findAllLegs();

	@Query("SELECT DISTINCT fa.legRelated FROM FlightAssignment fa WHERE fa.flightCrewMemberAssigned.id = :memberId and fa.id != :faId ")
	List<Leg> findLegsByFlightCrewMemberId(int memberId, int faId);

	@Query("select fa from FlightAssignment fa where fa.flightCrewsDuty =  acme.entities.flightAssignment.FlightCrewsDuty.PILOT and fa.legRelated.id = :idLeg and fa.draftMode = false")
	List<FlightAssignment> findPilotInLeg(int idLeg);

	@Query("select fa from FlightAssignment fa where fa.flightCrewsDuty =  acme.entities.flightAssignment.FlightCrewsDuty.CO_PILOT and fa.legRelated.id = :idLeg and fa.draftMode = false")
	List<FlightAssignment> findCoPilotInLeg(int idLeg);

}
