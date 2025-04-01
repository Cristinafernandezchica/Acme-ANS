
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class MaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	private MaintenanceRecordListService	listService;

	@Autowired
	private MaintenanceRecordCreateService	createService;

	@Autowired
	private MaintenanceRecordShowService	showService;

	@Autowired
	private MaintenanceRecordUpdateService	updateService;

	@Autowired
	private MaintenanceRecordPublisService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
