
package acme.features.authenticated.flightCrewMember;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.userAccount.id = :id")
	FlightCrewMember findFCMByUserAccountId(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFCMById(int id);

	@Query("SELECT fcm.employeeCode FROM FlightCrewMember fcm WHERE fcm.employeeCode LIKE CONCAT(:prefix, '%')")
	List<String> findAllIdentifiersStartingWith(@Param("prefix") String prefix);

	@Query("select a from Airline a")
	List<Airline> findAllAirlines();

}
