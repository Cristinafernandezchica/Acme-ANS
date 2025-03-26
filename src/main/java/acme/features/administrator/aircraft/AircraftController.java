
package acme.features.administrator.aircraft;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.Aircraft;

@GuiController
public class AircraftController extends AbstractGuiController<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private AircraftCreateService	createService;

	@Autowired
	private AircraftUpdateService	updateService;

	@Autowired
	private AicraftListService		listService;

	@Autowired
	private AircraftShowService		showService;

	// Constructors ---------------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("list", this.listService);
	}

}
