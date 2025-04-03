
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
public class MaintenanceRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	//Internal state ----------------------------------------------------------

	@Autowired
	private MaintenanceRecordRepository repository;

	//AbstractGuiService state ----------------------------------------------------------


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
		int id;

		id = super.getRequest().getData("id", int.class);
		mr = this.repository.findMRById(id);

		super.getBuffer().addData(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		SelectChoices statuses;
		SelectChoices choiceAircraft;
		Collection<Aircraft> aircrafts;

		Dataset dataset;
		aircrafts = this.repository.findAllAircraft();
		statuses = SelectChoices.from(Status.class, mr.getStatus());

		choiceAircraft = SelectChoices.from(aircrafts, "id", mr.getAircraft());

		dataset = super.unbindObject(mr, "moment", "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft", "draftMode");
		dataset.put("statuses", statuses);
		dataset.put("aircrafts", choiceAircraft);

		super.getResponse().addData(dataset);
	}
}
