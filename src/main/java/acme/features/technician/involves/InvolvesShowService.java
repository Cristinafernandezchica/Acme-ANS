
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class InvolvesShowService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private InvolvesRepository repository;

	//AbstractGuiService state ----------------------------------------------------------


	@Override
	public void authorise() {
		boolean authored = false;
		Involves involves;
		boolean showCreate;
		int involvesId;
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			involvesId = super.getRequest().getData("id", int.class);
			Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

			involves = this.repository.findInvolvesById(involvesId);
			showCreate = involves.getMaintenanceRecord().isDraftMode();
			if (involves != null && involves.getMaintenanceRecord().getTechnician().equals(technician))
				authored = true;
			super.getResponse().addGlobal("showCreate", showCreate);
			super.getResponse().setAuthorised(authored);

		}

	}

	@Override
	public void load() {
		Involves involves;
		int id;

		id = super.getRequest().getData("id", int.class);
		involves = this.repository.findInvolvesById(id);

		super.getBuffer().addData(involves);
	}

	@Override
	public void unbind(final Involves involves) {

		SelectChoices selectTask;

		Collection<Task> tasks;
		Dataset dataset;

		tasks = this.repository.findAllTasks();

		selectTask = SelectChoices.from(tasks, "taskLabel", involves.getTask());

		dataset = super.unbindObject(involves, "task");
		dataset.put("tasks", selectTask);
		dataset.put("task", selectTask.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
