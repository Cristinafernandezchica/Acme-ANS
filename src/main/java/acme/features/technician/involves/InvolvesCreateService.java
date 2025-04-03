
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class InvolvesCreateService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private InvolvesRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean authored = false;
		MaintenanceRecord mr;
		int maintenanceRecordId;
		maintenanceRecordId = super.getRequest().getData("id", int.class);
		Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		mr = this.repository.findMRById(maintenanceRecordId);

		if (mr.getTechnician().equals(technician))
			authored = true;

		super.getResponse().setAuthorised(authored);
	}

	@Override
	public void load() {
		Involves involves;

		int mrId = super.getRequest().getData("id", int.class);

		MaintenanceRecord mr = this.repository.findMRById(mrId);

		involves = new Involves();
		involves.setMaintenanceRecord(mr);
		super.getBuffer().addData(involves);
	}

	@Override
	public void bind(final Involves involves) {
		super.bindObject(involves, "task");
	}

	@Override
	public void validate(final Involves involves) {
		if (!this.getBuffer().getErrors().hasErrors("task") && involves.getTask() != null)
			super.state(!involves.getTask().isDraftMode(), "task", "acme.validation.technician.involves.message", involves);
		Task newTask = super.getRequest().getData("task", Task.class);
		Collection<Task> publishTasks = this.repository.findAllPublishTasks();
		if (!this.getBuffer().getErrors().hasErrors("task") && involves.getTask() != null)
			super.state(newTask != null || publishTasks.contains(newTask), "task", "acme.validation.technician.invalidTask.message", involves);
	}

	@Override
	public void perform(final Involves involves) {
		this.repository.save(involves);
	}

	@Override
	public void unbind(final Involves involves) {

		SelectChoices choiceTask;
		Dataset dataset;
		Collection<Task> tasks;

		tasks = this.repository.findAllPublishTasks();
		choiceTask = SelectChoices.from(tasks, "id", involves.getTask());

		dataset = super.unbindObject(involves, "task");

		dataset.put("tasks", choiceTask);
		dataset.put("task", choiceTask.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
