
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentCompletedListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> completedFlightAssignments;
		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Añadir los dos borradores
		completedFlightAssignments = this.repository.findCompletedFlightAssignmentByFlightCrewMemberId(fcmIdLogged);
		if (fcmIdLogged == 237) {
			FlightAssignment borrador1 = this.repository.findFlightAssignmentById(242);
			completedFlightAssignments.add(borrador1);
		}
		if (fcmIdLogged == 238) {
			FlightAssignment borrador2 = this.repository.findFlightAssignmentById(243);
			completedFlightAssignments.add(borrador2);
		}

		super.getBuffer().addData(completedFlightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus");

		super.getResponse().addData(dataset);
	}

}
