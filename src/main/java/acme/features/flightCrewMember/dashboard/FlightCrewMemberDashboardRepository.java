
package acme.features.flightCrewMember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	// Last 5 destinations

	@Query("SELECT l.arrivalAirport.name FROM FlightAssignment fa JOIN fa.legRelated l WHERE fa.flightCrewMemberAssigned.id = :crewMemberId ORDER BY fa.lastUpdate DESC")
	List<String> findLastDestinations(int crewMemberId);

	// Number of incidents classified by their severity level

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 0 AND 3 THEN l.id END) FROM ActivityLog al JOIN al.flightAssignmentRelated.legRelated l")
	Integer findLegsCountBySeverityLevelsLow();

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 4 AND 7 THEN l.id END) FROM ActivityLog al JOIN al.flightAssignmentRelated.legRelated l")
	Integer findLegsCountBySeverityLevelsMedium();

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 8 AND 10 THEN l.id END) FROM ActivityLog al JOIN al.flightAssignmentRelated.legRelated l")
	Integer findLegsCountBySeverityLevelsHigh();

	// Crew members from the last leg

	@Query("SELECT DISTINCT fa.flightCrewMemberAssigned FROM FlightAssignment fa WHERE fa.legRelated.id = (SELECT MAX(fa2.legRelated.id) FROM FlightAssignment fa2 WHERE fa2.flightCrewMemberAssigned.id = :crewMemberId)")
	List<FlightCrewMember> findCrewNamesInLastLeg(int crewMemberId);

	// Flight assignments classified by their status

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMemberAssigned.id =:crewMemberId and fa.currentStatus =:estado")
	List<FlightAssignment> findFlightAssignmentsByCrewMember(int crewMemberId, CurrentStatus estado);

	// Deviations, number of flights

	@Query("SELECT AVG(COUNT(fa)), MIN(COUNT(fa)), MAX(COUNT(fa)), STDDEV(COUNT(fa)) FROM FlightAssignment fa WHERE fa.lastUpdate BETWEEN :startDate AND :endDate AND fa.flightCrewMemberAssigned.id = :crewMemberId")
	List<Integer> calculateFlightAssignmentStats(Date startDate, Date endDate, int crewMemberId);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.lastUpdate BETWEEN :startDate AND :endDate AND fa.flightCrewMemberAssigned.id = :crewMemberId GROUP BY FUNCTION('DATE', fa.lastUpdate)")
	List<Long> getDailyAssignmentCounts(Date startDate, Date endDate, int crewMemberId);

}
