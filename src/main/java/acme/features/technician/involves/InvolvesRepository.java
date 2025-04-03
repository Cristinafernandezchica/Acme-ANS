
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.tasks.Task;

@Repository
public interface InvolvesRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecord.id = :mrId")
	Collection<Involves> findAllInvolvesByMRId(int mrId);

	@Query("select i from Involves i where i.id = :id")
	Involves findInvolvesById(int id);

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findAllPublishTasks();

	@Query("select t from Task t")
	Collection<Task> findAllTasks();

	@Query("select mr from MaintenanceRecord mr where mr.id = :mrId")
	MaintenanceRecord findMRById(int mrId);
}
