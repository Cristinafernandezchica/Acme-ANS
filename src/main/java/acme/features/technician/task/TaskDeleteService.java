
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class TaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TaskRepository repository;


	@Override
	public void authorise() {
		Task task;
		Technician technician;
		int taskId;
		boolean authored = false;
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			taskId = super.getRequest().getData("id", int.class);
			task = this.repository.findByTaskId(taskId);

			technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
			if (technician.equals(task.getTechnician()) && task.isDraftMode())
				authored = true;
		}
		super.getResponse().setAuthorised(authored);
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
	public void bind(final Task task) {
		;
	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task task) {

		Collection<Involves> involves;
		involves = this.repository.findAllInvolvesByTaskId(task.getId());

		this.repository.deleteAll(involves);
		this.repository.delete(task);
	}

	@Override
	public void unbind(final Task task) {
		;
	}
}
