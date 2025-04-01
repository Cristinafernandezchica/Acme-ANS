
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class TaskListService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<Task> task;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		task = this.repository.findAllTaskByTechnicianId(technicianId);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {

		Dataset dataset;

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");

		super.getResponse().addData(dataset);
	}
}
