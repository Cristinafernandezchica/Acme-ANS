
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.realms.Technician;

@GuiService
public class InvolvesListService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private InvolvesRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<Involves> involves;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		involves = this.repository.findAllInvolvesByMRId(maintenanceRecordId);

		super.getBuffer().addData(involves);
	}

	@Override
	public void unbind(final Involves involves) {

		Dataset dataset;

		dataset = super.unbindObject(involves, "tasks.id", "tasks.type", "tasks.priority");

		super.getResponse().addData(dataset);
	}
}
