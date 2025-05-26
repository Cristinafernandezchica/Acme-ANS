
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMemberAssigned.id = :fcmId")
	Collection<FlightAssignment> findFlightAssignmentsByFCMId(int fcmId);

	@Query("select al.flightAssignmentRelated from ActivityLog al where al.id = :id")
	FlightAssignment findFlightAssignmentByActivityLogId(int id);

	@Query("select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al where al.flightAssignmentRelated.id = :faId")
	Collection<ActivityLog> findActivityLogsByFAId(int faId);

	@Query("select al from ActivityLog al where al.flightAssignmentRelated.id = :faId and al.flightAssignmentRelated.flightCrewMemberAssigned.id = :fcmId")
	Collection<ActivityLog> findOwnedActivityLogsByFAId(int faId, int fcmId);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlighCrewMemberById(int id);

	@Query("select fa from FlightAssignment fa")
	List<FlightAssignment> findAllFlightAssignments();

	@Query("select al from ActivityLog al")
	List<ActivityLog> findAllActivityLog();

}
