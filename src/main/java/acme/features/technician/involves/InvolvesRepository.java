
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.tasks.Task;

@Repository
public interface InvolvesRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecords.id = :mrId")
	Collection<Involves> findAllInvolvesByMRId(int mrId);

	@Query("select i from Involves i where i.id = :id")
	Involves findInvolvesById(int id);

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findAllPublishTasks();

}
