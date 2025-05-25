
package acme.features.flightCrewMember.flightAssigment;

import java.util.Arrays;
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
		boolean authorization;

		FlightCrewMember fcmLogged;
		FlightAssignment faSelected;

		boolean existingFA = false;
		boolean isFlightAssignmentOwner = false;
		boolean isPublished = false;
		String metodo = super.getRequest().getMethod();
		boolean falsePublish = false;
		Integer faId;
		boolean validLeg = true;
		boolean validDuty = true;
		boolean validStatus = true;

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			falsePublish = true;
			faId = super.getRequest().getData("id", Integer.class);
			if (faId != null) {
				fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
				faSelected = this.repository.findFlightAssignmentById(faId);
				existingFA = faSelected != null;
				if (existingFA) {
					isFlightAssignmentOwner = faSelected.getFlightCrewMemberAssigned() == fcmLogged;
					if (metodo.equals("GET"))
						isPublished = !faSelected.isDraftMode();
				}
			}
			if (metodo.equals("POST")) {
				Integer legId = super.getRequest().getData("legRelated", Integer.class);

				if (legId == null)
					validLeg = false;
				else {
					Leg leg = this.repository.findLegById(legId);
					if (leg == null && legId != 0)
						validLeg = false;
				}

				String duty = super.getRequest().getData("flightCrewsDuty", String.class);
				if (duty == null || Arrays.stream(FlightCrewsDuty.values()).noneMatch(tc -> tc.name().equals(duty)) && !duty.equals("0"))
					validDuty = false;

				String currentStatus = super.getRequest().getData("currentStatus", String.class);
				if (currentStatus == null || Arrays.stream(CurrentStatus.values()).noneMatch(cs -> cs.name().equals(currentStatus)) && !currentStatus.equals("0"))
					validStatus = false;

			}
		}

		authorization = isFlightAssignmentOwner && validLeg && validDuty && validStatus && !isPublished && falsePublish;
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
		FlightCrewMember flightCrewMember;

		int legId;
		Leg leg;

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightCrewMember = this.repository.findFlighCrewMemberById(fcmIdLogged);

		legId = super.getRequest().getData("legRelated", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "flightCrewsDuty", "currentStatus", "remarks");
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
		if (isLegNull) {

			// Comprobación de leg no pasada
			boolean legNotPast;
			legNotPast = flightAssignment.getLegRelated().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(!legNotPast, "legRelated", "acme.validation.legNotPast.message");

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
			super.state(fcmAvailable, "*", "acme.validation.fcmAvailable-publish.message");

			// Comprobación de que el estado del FA sea CONFIRMED o CANCELLED
			boolean isConfirmedOrCancelled = true;
			if (flightAssignment.getCurrentStatus() != null) {
				isConfirmedOrCancelled = flightAssignment.getCurrentStatus().equals(CurrentStatus.CONFIRMED) || flightAssignment.getCurrentStatus().equals(CurrentStatus.CANCELLED);
				super.state(isConfirmedOrCancelled, "currentStatus", "acme.validation.isConfirmedOrCancelled.message");
			}

			// Comprobación de 1 piloto y un copiloto
			boolean pilotExceptionPassed = true;
			boolean coPilotExceptionPassed = true;
			if (flightAssignment.getFlightCrewsDuty() != null) {
				Integer fAHasPilot = this.repository.findPilotInLeg(flightAssignment.getLegRelated().getId()).size();
				Integer fAHasCoPilot = this.repository.findCoPilotInLeg(flightAssignment.getLegRelated().getId()).size();

				FlightCrewsDuty flightCrewsDuty = flightAssignment.getFlightCrewsDuty();
				if (flightCrewsDuty.equals(FlightCrewsDuty.PILOT) && fAHasPilot >= 1 && !flightAssignment.getCurrentStatus().equals(CurrentStatus.CANCELLED))
					pilotExceptionPassed = false;
				else if (flightCrewsDuty.equals(FlightCrewsDuty.CO_PILOT) && fAHasCoPilot >= 1 && !flightAssignment.getCurrentStatus().equals(CurrentStatus.CANCELLED))
					coPilotExceptionPassed = false;

				super.state(pilotExceptionPassed, "legRelated", "acme.validation.pilotExceptionPassed.message");
				super.state(coPilotExceptionPassed, "legRelated", "acme.validation.coPilotExceptionPassed.message");
			}

			// Comprobación de leg asignadas simultáneamente
			boolean legCompatible = true;

			List<Leg> legByFCM = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMemberAssigned().getId(), flightAssignment.getId()).stream().toList();
			for (Leg l : legByFCM)
				if (this.legIsCompatible(flightAssignment, l)) {
					legCompatible = false;
					super.state(legCompatible, "legRelated", "acme.validation.legCompatible.message");
					break;
				}

			boolean confirmation;
			confirmation = super.getRequest().getData("confirmation", boolean.class);
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		}

	}

	private boolean legIsCompatible(final FlightAssignment fa, final Leg legToCompare) {
		Leg finalLeg = fa.getLegRelated();
		boolean departureCompatible = MomentHelper.isInRange(finalLeg.getScheduledDeparture(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		boolean arrivalCompatible = MomentHelper.isInRange(finalLeg.getScheduledArrival(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		return departureCompatible && arrivalCompatible;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		SelectChoices statuses;

		SelectChoices flightcrewsDuties;

		SelectChoices legChoices;
		List<Leg> legs;

		statuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		flightcrewsDuties = SelectChoices.from(FlightCrewsDuty.class, flightAssignment.getFlightCrewsDuty());

		legs = this.repository.findAllLegs();
		legChoices = SelectChoices.from(legs, "label", flightAssignment.getLegRelated());

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlighCrewMemberById(fcmIdLogged);

		dataset = super.unbindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("statuses", statuses);
		dataset.put("flightcrewsDuties", flightcrewsDuties);
		dataset.put("legRelated", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMemberAssigned", flightCrewMember);
		dataset.put("FCMname", flightCrewMember.getIdentity().getName() + " " + flightCrewMember.getIdentity().getSurname());
		dataset.put("lastUpdate", MomentHelper.getCurrentMoment());
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}

}
