
package acme.features.authenticated.assistanceAgent;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.assistanceAgents.AssistanceAgent;

@Repository
public interface AuthenticatedAssistanceAgentRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select airline from Airline airline where airline.id = :id")
	Airline findAirlineById(int id);

	@Query("select a from AssistanceAgent a where a.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);

	@Query("select a from AssistanceAgent a where a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("SELECT a.employeeCode from AssistanceAgent a WHERE a.employeeCode LIKE CONCAT(:prefix, '%')")
	List<String> findAllCodesStartingWith(@Param("prefix") String prefix);

	@Query("SELECT airline FROM Airline airline")
	Collection<Airline> findAllAirlines();

}
