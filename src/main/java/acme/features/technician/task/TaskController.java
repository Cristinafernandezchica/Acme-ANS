
package acme.features.technician.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiController
public class TaskController extends AbstractGuiController<Technician, Task> {

	@Autowired
	private TaskListService		listService;

	@Autowired
	private TaskShowService		showService;

	@Autowired
	private TaskCreateService	createService;

	@Autowired
	private TaskUpdateService	updateService;

	@Autowired
	private TaskPublishService	publishService;

	@Autowired
	private TaskDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);
	}
}
