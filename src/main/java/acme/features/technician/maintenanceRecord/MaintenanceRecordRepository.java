
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.tasks.Task;

@Repository
public interface MaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findAllMRByTechnicianId(final int technicianId);

	@Query("select mr from MaintenanceRecord mr")
	Collection<MaintenanceRecord> findAllMR();

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMRById(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircraft();

	@Query("select i from Involves i where i.maintenanceRecord.id = :mrId")
	Collection<Involves> findAllInvolvesByMRId(int mrId);

	@Query("select i.task from Involves i where i.id = :involveId")
	Task findTaskByInvolveId(int involveId);

}
