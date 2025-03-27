
package acme.features.any.service;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.service.Service;

@Repository
public interface ServiceRepository extends AbstractRepository {

	@Query("select s from Service s")
	Collection<Service> findAllService();

	@Query("SELECT DISTINCT s.picture FROM Service s")
	Collection<String> findDistinctPictures();

}
