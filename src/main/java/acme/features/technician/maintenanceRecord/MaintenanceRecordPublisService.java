
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.maintenanceRecords.Status;
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

		super.bindObject(mr);
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		int id;
		id = super.getRequest().getData("id", int.class);
		int unpublishedNumberTasks = this.repository.findAllInvolvesByMRId(id).stream().filter(x -> x.getTask().isDraftMode()).toList().size();

		int publishedNumberTasks = this.repository.findAllInvolvesByMRId(id).stream().filter(x -> x.getTask().isDraftMode() == false).toList().size();

		if (!this.getBuffer().getErrors().hasErrors("notes") && mr.isDraftMode() == true)
			super.state(unpublishedNumberTasks == 0, "notes", "acme.validation.technician.maintenance-record.unpublished-tasks.message", mr);
		if (!this.getBuffer().getErrors().hasErrors("notes") && mr.isDraftMode() == true)
			super.state(publishedNumberTasks > 0, "notes", "acme.validation.technician.maintenance-record.published-tasks.message", mr);

	}

	@Override
	public void perform(final MaintenanceRecord mr) {

		mr.setDraftMode(false);
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		SelectChoices choiceAircraft;
		SelectChoices statuses;
		Dataset dataset;
		Collection<Aircraft> aircrafts;

		statuses = SelectChoices.from(Status.class, mr.getStatus());
		aircrafts = this.repository.findAllAircraft();
		choiceAircraft = SelectChoices.from(aircrafts, "id", mr.getAircraft());

		dataset = super.unbindObject(mr, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft", "draftMode");

		dataset.put("statuses", statuses);
		dataset.put("aircrafts", choiceAircraft);

		dataset.put("aircraft", choiceAircraft.getSelected().getKey());
		dataset.put("status", statuses.getSelected().getKey());

		super.getResponse().addData(dataset);

	}

}
