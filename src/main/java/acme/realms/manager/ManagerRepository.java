
package acme.realms.manager;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface ManagerRepository extends AbstractRepository {

	@Query("SELECT m.identifierNumber FROM Manager m WHERE m.identifierNumber LIKE CONCAT(:prefix, '%')")
	List<String> findAllIdentifiersStartingWith(@Param("prefix") String prefix);

	@Query("select m from Manager m where m.identifierNumber = :identifierNumber")
	Manager findManagerByIdentifierNumber(String identifierNumber);

}
