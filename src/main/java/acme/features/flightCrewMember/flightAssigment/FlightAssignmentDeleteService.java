
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightCrewsDuty;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		FlightCrewMember fcmLogged;
		FlightAssignment faSelected;

		boolean existingFA = false;
		boolean isFlightAssignmentOwner = false;
		boolean isPublished = false;
		boolean falseDelete = false;
		Integer faId;

		String metodo = super.getRequest().getMethod();

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			falseDelete = true;
			faId = super.getRequest().getData("id", Integer.class);
			if (faId != null) {
				fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
				List<FlightAssignment> allFA = this.repository.findAllFlightAssignments();
				faSelected = this.repository.findFlightAssignmentById(faId);
				existingFA = faSelected != null || allFA.contains(faSelected) && faSelected != null;
				if (existingFA) {
					isFlightAssignmentOwner = faSelected.getFlightCrewMemberAssigned() == fcmLogged;
					if (metodo.equals("GET"))
						isPublished = !faSelected.isDraftMode();
				}
			}
		}

		super.getResponse().setAuthorised(isFlightAssignmentOwner && !isPublished && falseDelete);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int faIdSolicitud;

		faIdSolicitud = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(faIdSolicitud);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks", "legRelated", "flightCrewMemberAssigned");

	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		//List<ActivityLog> logs = this.repository.findActivityLogsByFAId(flightAssignment.getId());
		//this.repository.deleteAll(logs);
		this.repository.delete(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		SelectChoices statuses;

		SelectChoices flightcrewsDuties;

		SelectChoices legChoices;
		List<Leg> legs;

		SelectChoices flightCrewMemberChoices;
		Collection<FlightCrewMember> availableFlightCrewMembers;

		statuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		flightcrewsDuties = SelectChoices.from(FlightCrewsDuty.class, flightAssignment.getFlightCrewsDuty());

		legs = this.repository.findAllLegs();
		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLegRelated());

		availableFlightCrewMembers = this.repository.findAvailableFlightCrewMembers();
		flightCrewMemberChoices = SelectChoices.from(availableFlightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMemberAssigned());

		dataset = super.unbindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("statuses", statuses);
		dataset.put("flightcrewsDuties", flightcrewsDuties);
		dataset.put("legRelated", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMemberAssigned", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("availableFlightCrewMembers", flightCrewMemberChoices);
		dataset.put("lastUpdate", MomentHelper.getCurrentMoment());

		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
