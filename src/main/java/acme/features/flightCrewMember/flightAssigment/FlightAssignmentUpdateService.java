
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
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {

		boolean authorization;
		boolean status;
		boolean isFCMAssignedToTheFA;
		int fcmIdLogged;
		FlightCrewMember fcmLogged;
		FlightAssignment flightAssignment;
		int faIdSolicitud;

		fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);

		faIdSolicitud = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(faIdSolicitud);

		status = flightAssignment.isDraftMode();

		isFCMAssignedToTheFA = flightAssignment.getFlightCrewMemberAssigned().equals(fcmLogged);

		authorization = status && isFCMAssignedToTheFA;

		super.getResponse().setAuthorised(authorization);

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
		int flightCrewMemberId;
		FlightCrewMember flightCrewMember;

		int legId;
		Leg leg;

		flightCrewMemberId = super.getRequest().getData("flightCrewMemberAssigned", int.class);
		flightCrewMember = this.repository.findFlighCrewMemberById(flightCrewMemberId);

		legId = super.getRequest().getData("legRelated", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks");
		flightAssignment.setFlightCrewMemberAssigned(flightCrewMember);
		flightAssignment.setLegRelated(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		boolean confirmation;
		boolean pilotExceptionPassed = true;
		boolean coPilotExceptionPassed = true;

		// Comprobación de 1 piloto y un copiloto
		boolean fAHasPilot = this.repository.findPilotInLeg(flightAssignment.getId());
		boolean fAHasCoPilot = this.repository.findCoPilotInLeg(flightAssignment.getId());

		FlightCrewsDuty flightCrewsDuty = flightAssignment.getFlightCrewsDuty();
		if (flightCrewsDuty.equals(FlightCrewsDuty.PILOT) && fAHasPilot)
			pilotExceptionPassed = false;
		if (flightCrewsDuty.equals(FlightCrewsDuty.CO_PILOT) && fAHasCoPilot)
			coPilotExceptionPassed = false;

		super.state(pilotExceptionPassed, "pilotExceptionPassed", "acme.validation.pilotExceptionPassed.message");
		super.state(coPilotExceptionPassed, "coPilotExceptionPassed", "acme.validation.coPilotExceptionPassed.message");

		// Comprobación de leg asignadas simultáneamente
		boolean legCompatible = true;

		List<Leg> legByFCM = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMemberAssigned().getId());
		for (Leg l : legByFCM)
			if (!this.legIsCompatible(flightAssignment.getLegRelated(), l)) {
				legCompatible = false;
				super.state(legCompatible, "legCompatible", "acme.validation.legCompatible.message");
				break;
			}

		/*
		 * Falta:
		 * - Comprobacion de la leg no pasada
		 * - Comprobacion de la FCM esta available
		 * - Comprobacion de los otros atributos
		 */

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	private boolean legIsCompatible(final Leg finalLeg, final Leg legToCompare) {
		boolean departureCompatible = MomentHelper.isInRange(finalLeg.getScheduledDeparture(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		boolean arrivalCompatible = MomentHelper.isInRange(finalLeg.getScheduledArrival(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		return departureCompatible && arrivalCompatible;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
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
