
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class MaintenanceRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private MaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<MaintenanceRecord> mr;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		mr = this.repository.findAllMRByTechnicianId(technicianId);

		super.getBuffer().addData(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		Dataset dataset;

		dataset = super.unbindObject(mr, "aircraft.model", "moment", "status", "inspectionDueDate", "estimatedCost", "notes", "draftMode");

		super.getResponse().addData(dataset);
	}
}
