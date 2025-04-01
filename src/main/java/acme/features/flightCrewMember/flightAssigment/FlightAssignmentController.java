
package acme.features.flightCrewMember.flightAssigment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private FlightAssignmentPlannedListService		plannedListService;

	@Autowired
	private FlightAssignmentCompletedListService	completedListService;

	@Autowired
	private FlightAssignmentShowService				showService;

	@Autowired
	private FlightAssignmentCreateService			createService;

	@Autowired
	private FlightAssignmentUpdateService			updateService;

	// Constructors ---------------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("planned-list", "list", this.plannedListService);
		super.addCustomCommand("completed-list", "list", this.completedListService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
