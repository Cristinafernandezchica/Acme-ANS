
package acme.realms.customer;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CustomerRepository extends AbstractRepository {

	@Query("SELECT c.identifier FROM Customer c WHERE c.identifier LIKE CONCAT(:prefix, '%')")
	List<String> findAllIdentifiersStartingWith(@Param("prefix") String prefix);

	@Query("SELECT c FROM Customer c WHERE c.identifier = :identifier")
	Customer findByIdentifier(@Param("identifier") String identifier);

}
