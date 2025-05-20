
package acme.entities.systemCurrencies;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface SystemCurrenciesRepository extends AbstractRepository {

	@Query("select sC from SystemCurrencies sC")
	List<SystemCurrencies> findAllCurrencies();

}
