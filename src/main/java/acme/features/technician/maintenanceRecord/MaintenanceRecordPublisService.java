
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class MaintenanceRecordPublisService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private MaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		MaintenanceRecord mr;
		Technician technician;
		int mrId;

		mrId = super.getRequest().getData("id", int.class);
		mr = this.repository.findMRById(mrId);

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		if (technician.equals(mr.getTechnician()) && mr.isDraftMode())
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMRById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord mr) {

		super.bindObject(mr, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord mr) {

		mr.setDraftMode(false);
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		Dataset dataset;

		dataset = super.unbindObject(mr, "draftMode");

		super.getResponse().addData(dataset);

	}

}
