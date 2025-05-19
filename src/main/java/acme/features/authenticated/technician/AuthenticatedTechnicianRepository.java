
package acme.features.authenticated.technician;

import org.springframework.data.jpa.repository.Query;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.Technician;

public interface AuthenticatedTechnicianRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select t from Technician t where t.userAccount.id = :id")
	Technician findTechnicianByUserAccountId(int id);

	@Query("select t from Technician t where t.id = :id")
	Technician findTechnicianById(int id);
}
