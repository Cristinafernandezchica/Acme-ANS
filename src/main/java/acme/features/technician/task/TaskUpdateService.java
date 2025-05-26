
package acme.features.technician.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.entities.tasks.Type;
import acme.realms.Technician;

@GuiService
public class TaskUpdateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TaskRepository repository;


	@Override
	public void authorise() {
		Task task;
		Technician technician;
		int taskId;
		boolean authored = false;
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null && !(super.getRequest().getData().size() <= 1)) {
			taskId = super.getRequest().getData("id", int.class);
			task = this.repository.findByTaskId(taskId);

			technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
			if (task != null && technician.equals(task.getTechnician()) && task.isDraftMode())
				authored = true;

			if (super.getRequest().getMethod().equals("POST"))
				if (authored) {
					String newType = super.getRequest().getData("type", String.class);
					List<String> listaDeStatus = List.of(Type.INSPECTION.name(), Type.MAINTENANCE.name(), Type.REPAIR.name(), Type.SYSTEMCHECK.name());
					if (!newType.equals("0") && (newType == null || !listaDeStatus.contains(newType)))
						authored = false;
				}
		}
		super.getResponse().setAuthorised(authored);
	}

	@Override
	public void load() {
		Task task;
		int taskId;
		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findByTaskId(taskId);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {

		super.bindObject(task, "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task task) {
		this.repository.save(task);
	}

	@Override
	public void unbind(final Task task) {

		SelectChoices types;
		Dataset dataset;

		types = SelectChoices.from(Type.class, task.getType());

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");

		dataset.put("types", types);
		dataset.put("type", types.getSelected().getKey());

		super.getResponse().addData(dataset);

	}
}
