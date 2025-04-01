
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;
import java.util.Date;

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
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		flightAssignment = new FlightAssignment();

		flightAssignment.setDraftMode(true);
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		int flightCrewMemberId;
		FlightCrewMember flightCrewMember;
		int legId;
		Leg leg;

		flightCrewMemberId = super.getRequest().getData("airline", int.class);
		flightCrewMember = this.repository.findFlighCrewMemberById(flightCrewMemberId);

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks", "legRelated", "flightCrewMemberAssigned");
		flightAssignment.setFlightCrewMemberAssigned(flightCrewMember);
		flightAssignment.setLegRelated(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean confirmation;
		boolean pilotExceptionPassed = true;
		boolean coPilotExceptionPassed = true;

		boolean fAHasPilot = this.repository.findPilotInLeg(flightAssignment.getId());
		boolean fAHasCoPilot = this.repository.findCoPilotInLeg(flightAssignment.getId());

		FlightCrewsDuty flightCrewsDuty = flightAssignment.getFlightCrewsDuty();
		if (flightCrewsDuty.equals(FlightCrewsDuty.PILOT) && fAHasPilot)
			pilotExceptionPassed = false;
		else if (flightCrewsDuty.equals(FlightCrewsDuty.CO_PILOT) && fAHasCoPilot)
			coPilotExceptionPassed = false;

		super.state(pilotExceptionPassed, "pilotExceptionPassed", "acme.validation.pilotExceptionPassed.message");
		super.state(coPilotExceptionPassed, "coPilotExceptionPassed", "acme.validation.coPilotExceptionPassed.message");

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices statuses;
		SelectChoices flightcrewsDuties;
		Collection<FlightCrewMember> flightCrewMembers;
		SelectChoices selectedFlightCrewMember;
		Collection<Leg> legs;
		SelectChoices selectedLeg;

		Date currentDate = MomentHelper.getCurrentMoment();

		statuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		flightcrewsDuties = SelectChoices.from(FlightCrewsDuty.class, flightAssignment.getFlightCrewsDuty());

		flightCrewMembers = this.repository.findAvailableFlightCrewMembers();
		selectedFlightCrewMember = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMemberAssigned());

		legs = this.repository.findAllLegs();
		selectedLeg = SelectChoices.from(legs, "flightNumber", flightAssignment.getLegRelated());

		dataset = super.unbindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks");

		dataset.put("statuses", statuses);
		dataset.put("selectedFlightCrewMember", selectedFlightCrewMember);
		dataset.put("selectedLeg", selectedLeg);
		dataset.put("flightcrewsDuties", flightcrewsDuties);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}
}
