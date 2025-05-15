
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
public class MaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private MaintenanceRecordRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean authored = true;
		if (super.getRequest().getMethod().equals("POST")) {
			if (authored) {
				String newStatus = super.getRequest().getData("status", String.class);
				List<String> listaDeStatus = List.of(Status.COMPLETED.name(), Status.INPROGRESS.name(), Status.PENDING.name());
				if (!newStatus.equals("0") && (newStatus == null || !listaDeStatus.contains(newStatus)))
					authored = false;
			}
			if (authored) {
				Integer newAircraft = super.getRequest().getData("aircraft", int.class);
				List<Integer> listaDeAircraft = this.repository.findAllAircraft().stream().map(x -> x.getId()).toList();
				if (newAircraft != 0 && (newAircraft == null || !listaDeAircraft.contains(newAircraft)))
					authored = false;
			}
		}
		super.getResponse().setAuthorised(authored);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Date moment;
		boolean draftMode;

		moment = MomentHelper.getCurrentMoment();
		Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		draftMode = true;

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setMoment(moment);
		maintenanceRecord.setDraftMode(draftMode);
		super.getBuffer().addData(maintenanceRecord);
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
		choiceAircraft = SelectChoices.from(aircrafts, "aircraftLabel", mr.getAircraft());

		dataset = super.unbindObject(mr, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft", "moment");

		dataset.put("statuses", statuses);
		dataset.put("aircrafts", choiceAircraft);

		dataset.put("aircraft", choiceAircraft.getSelected().getKey());
		dataset.put("status", statuses.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
