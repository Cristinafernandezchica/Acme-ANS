
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.maintenanceRecords.Status;
import acme.realms.Technician;

@GuiService
public class MaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
		if (technician.equals(mr.getTechnician()))
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		int mrId;
		Status newStatus;
		mrId = super.getRequest().getData("id", int.class);
		mr = this.repository.findMRById(mrId);

		newStatus = super.getRequest().getData("status", Status.class);

		//como moment representa el momento en que una tarea cambia de status
		//pues si cambia el status cambia el moment
		if (!mr.getStatus().equals(newStatus)) {
			Date newMoment = MomentHelper.getCurrentMoment();
			mr.setMoment(newMoment);
		}
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord mr) {

		super.bindObject(mr, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		if (!this.getBuffer().getErrors().hasErrors("inspectionDate") && mr.getInspectionDueDate() != null)
			super.state(mr.getInspectionDueDate().after(mr.getMoment()), "inspectionDueDate", "acme.validation.service.inspectionDueDate.message", mr);
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
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

		dataset = super.unbindObject(mr, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft", "moment", "draftMode");

		dataset.put("statuses", statuses);
		dataset.put("aircrafts", choiceAircraft);

		dataset.put("aircraft", choiceAircraft.getSelected().getKey());
		dataset.put("status", statuses.getSelected().getKey());

		super.getResponse().addData(dataset);

	}
}
