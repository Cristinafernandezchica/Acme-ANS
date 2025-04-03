
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.entities.tasks.Type;
import acme.realms.Technician;

@GuiService
public class TaskShowService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TaskRepository repository;

	//AbstractGuiService state ----------------------------------------------------------


	@Override
	public void authorise() {
		Task task;
		Technician technician;
		int taskId;

		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findByTaskId(taskId);

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		if (technician.equals(task.getTechnician()))
			super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findByTaskId(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {

		SelectChoices types;

		Dataset dataset;
		types = SelectChoices.from(Type.class, task.getType());

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("types", types);

		super.getResponse().addData(dataset);
	}
}
