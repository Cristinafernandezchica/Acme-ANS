
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
import acme.entities.legs.LegStatus;
import acme.realms.flightCrewMember.AvailabilityStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean isFlightAssignmentOwner;

		FlightCrewMember fcmLogged;
		FlightAssignment faSelected;
		int faId = super.getRequest().getData("id", int.class);

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
		faSelected = this.repository.findFlightAssignmentById(faId);
		isFlightAssignmentOwner = faSelected.getFlightCrewMemberAssigned() == fcmLogged;

		super.getResponse().setAuthorised(isFlightAssignmentOwner);
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
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());
		flightAssignment.setFlightCrewMemberAssigned(flightCrewMember);
		flightAssignment.setLegRelated(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		FlightCrewMember fcmLogged;
		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);

		// Comprobación de leg no null
		boolean isLegNull;
		isLegNull = flightAssignment.getLegRelated() != null;
		if (!isLegNull)
			throw new IllegalStateException("That leg doesn't exist");
		else {

			// Comprobación de FCM no modificado
			boolean isOriginalFCM;
			isOriginalFCM = flightAssignment.getFlightCrewMemberAssigned() == fcmLogged;
			super.state(isOriginalFCM, "flightCrewMemberAssigned", "acme.validation.isOriginalFCM.message");

			// Comprobación de leg no pasada
			boolean legNotPast;
			legNotPast = flightAssignment.getLegRelated().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(legNotPast, "legRelated", "acme.validation.legNotPast.message");

			// Comprobación de leg no completada
			boolean legNotCompleted;
			legNotCompleted = flightAssignment.getLegRelated().getStatus().equals(LegStatus.ON_TIME) || flightAssignment.getLegRelated().getStatus().equals(LegStatus.DELAYED);
			super.state(legNotCompleted, "legRelated", "acme.validation.legNotCompleted.message");

			// Comprobación de leg no publicada
			boolean legNotPublished;
			legNotPublished = !flightAssignment.getLegRelated().isDraftMode();
			super.state(legNotPublished, "legRelated", "acme.validation.legNotPublished.message");

			// Comprobación de leg operada con la aerolínea del FCM
			boolean legFromRightAirline;
			legFromRightAirline = flightAssignment.getLegRelated().getAircraft().getAirline().equals(fcmLogged.getAirline());
			super.state(legFromRightAirline, "legRelated", "acme.validation.legFromRightAirline.message");

			// Comprobación de que el FCM esté AVAILABLE
			boolean fcmAvailable;
			fcmAvailable = flightAssignment.getFlightCrewMemberAssigned().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
			super.state(fcmAvailable, "legRelated", "acme.validation.fcmAvailable.message");

			// Comprobación de que el estado del FA sea CONFIRMED o CANCELLED
			boolean isConfirmedOrCancelled;
			isConfirmedOrCancelled = flightAssignment.getCurrentStatus().equals(CurrentStatus.CONFIRMED) || flightAssignment.getCurrentStatus().equals(CurrentStatus.CANCELLED);
			super.state(isConfirmedOrCancelled, "currentStatus", "acme.validation.isConfirmedOrCancelled.message");

			// Comprobación de 1 piloto y un copiloto
			boolean pilotExceptionPassed = true;
			boolean coPilotExceptionPassed = true;
			boolean fAHasPilot = this.repository.findPilotInLeg(flightAssignment.getId());
			boolean fAHasCoPilot = this.repository.findCoPilotInLeg(flightAssignment.getId());

			FlightCrewsDuty flightCrewsDuty = flightAssignment.getFlightCrewsDuty();
			if (flightCrewsDuty.equals(FlightCrewsDuty.PILOT) && fAHasPilot)
				pilotExceptionPassed = false;
			if (flightCrewsDuty.equals(FlightCrewsDuty.CO_PILOT) && fAHasCoPilot)
				coPilotExceptionPassed = false;

			super.state(pilotExceptionPassed, "legRelated", "acme.validation.pilotExceptionPassed.message");
			super.state(coPilotExceptionPassed, "legRelated", "acme.validation.coPilotExceptionPassed.message");

			// Comprobación de leg asignadas simultáneamente
			boolean legCompatible = true;

			List<Leg> legByFCM = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMemberAssigned().getId());
			for (Leg l : legByFCM)
				if (this.legIsCompatible(flightAssignment.getLegRelated(), l)) {
					legCompatible = false;
					super.state(legCompatible, "legRelated", "acme.validation.legCompatible.message");
					break;
				}

			boolean confirmation;
			confirmation = super.getRequest().getData("confirmation", boolean.class);
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		}

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
