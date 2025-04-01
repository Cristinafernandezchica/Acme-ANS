
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.tasks.Task;

@Repository
public interface TaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.technician.id = :technicianId")
	Collection<Task> findAllTaskByTechnicianId(int technicianId);

	@Query("select t from Task t where t.id = :taskId")
	Task findByTaskId(int taskId);

}
