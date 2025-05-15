
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
public class TaskCreateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TaskRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean authored = true;
		if (super.getRequest().getMethod().equals("POST"))
			if (authored) {
				String newType = super.getRequest().getData("type", String.class);
				List<String> listaDeStatus = List.of(Type.INSPECTION.name(), Type.MAINTENANCE.name(), Type.REPAIR.name(), Type.SYSTEMCHECK.name());
				if (!newType.equals("0") && (newType == null || !listaDeStatus.contains(newType)))
					authored = false;
			}
		super.getResponse().setAuthorised(authored);
	}

	@Override
	public void load() {
		Task task;
		boolean draftMode;

		Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		draftMode = true;

		task = new Task();
		task.setTechnician(technician);
		task.setDraftMode(draftMode);
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
