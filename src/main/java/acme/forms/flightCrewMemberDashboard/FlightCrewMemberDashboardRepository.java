
package acme.forms.flightCrewMemberDashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	// Last 5 destinations

	@Query("SELECT l.arrivalAirport.name FROM FlightAssignment fa JOIN fa.legRelated l WHERE fa.flightCrewMemberAssigned.id = :crewMemberId ORDER BY fa.lastUpdate DESC")
	List<String> findDestinations(@Param("crewMemberId") int crewMemberId);

	// Number of incidents classified by their severity level

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 0 AND 3 THEN l.id END), COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 4 AND 7 THEN l.id END), COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 8 AND 10 THEN l.id END) FROM ActivityLog al JOIN al.flightAssignmentRelated.legRelated l")
	Object[] findLegsCountBySeverityLevels();

	// Crew members from the last leg

	@Query("SELECT DISTINCT fa.flightCrewMemberAssigned FROM FlightAssignment fa WHERE fa.legRelated.id = (SELECT MAX(fa2.legRelated.id) FROM FlightAssignment fa2 WHERE fa2.flightCrewMemberAssigned.id = :crewMemberId)")
	List<FlightCrewMember> findCrewNamesInLastLeg(@Param("crewMemberId") int crewMemberId);

	// Flight assignments classified by their status

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMemberAssigned.id = :crewMemberId")
	List<FlightAssignment> findFlightAssignmentsByCrewMember(@Param("crewMemberId") int crewMemberId);

	// Deviations, number of flights

	@Query("SELECT AVG(COUNT(fa)), MIN(COUNT(fa)), MAX(COUNT(fa)), STDDEV(COUNT(fa)) FROM FlightAssignment fa WHERE fa.lastUpdate >= :lastMonth GROUP BY fa.flightCrewMemberAssigned")
	Object[] calculateFlightAssignmentStats(@Param("lastMonth") Date lastMonth);

}
