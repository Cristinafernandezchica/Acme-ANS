
package acme.features.flightCrewMember.flightAssigment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
public class FlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {

		// Comprobacion de leg
		String metodo = super.getRequest().getMethod();
		boolean authorised;

		boolean validLeg = true;
		boolean validDuty = true;
		boolean noId = true;
		boolean fakeUpdate = true;

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				fakeUpdate = false;
		}

		if (metodo.equals("POST")) {
			Integer legId = super.getRequest().getData("legRelated", Integer.class);

			if (legId == null)
				validLeg = false;
			else {
				Leg leg = this.repository.findLegById(legId);
				List<Leg> allLegs = this.repository.findAllLegs();
				if (leg == null && legId != 0 || !allLegs.contains(leg) && legId != 0)
					validLeg = false;
			}

			String duty = super.getRequest().getData("flightCrewsDuty", String.class);
			if (duty == null || duty.trim().isEmpty() || Arrays.stream(FlightCrewsDuty.values()).noneMatch(tc -> tc.name().equals(duty)) && !duty.equals("0"))
				validDuty = false;

		}

		authorised = validLeg && validDuty && fakeUpdate && noId;

		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		flightAssignment = new FlightAssignment();

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlighCrewMemberById(fcmIdLogged);

		flightAssignment.setDraftMode(true);
		flightAssignment.setFlightCrewMemberAssigned(flightCrewMember);
		flightAssignment.setCurrentStatus(CurrentStatus.PENDING);

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

		boolean isLegNull;
		isLegNull = flightAssignment.getLegRelated() != null;
		if (isLegNull) {

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

			//Comprobación de leg no en vuelo
			boolean legOnAir = false;
			if (flightAssignment.getLegRelated().getStatus().equals(LegStatus.ON_TIME) || flightAssignment.getLegRelated().getStatus().equals(LegStatus.DELAYED)) {
				Date departureTime = flightAssignment.getLegRelated().getScheduledDeparture();
				Date arrivalTime = flightAssignment.getLegRelated().getScheduledDeparture();
				legOnAir = MomentHelper.isInRange(MomentHelper.getCurrentMoment(), departureTime, arrivalTime);
			}
			super.state(!legOnAir, "legRelated", "acme.validation.legOnAir.message");

			// Comprobación de que el FCM esté AVAILABLE
			boolean fcmAvailable;
			fcmAvailable = flightAssignment.getFlightCrewMemberAssigned().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
			super.state(fcmAvailable, "legRelated", "acme.validation.fcmAvailable.message");

			// Comprobación de leg asignadas al fcm no sea a la vez que otra
			boolean legCompatible = true;

			List<Leg> legByFCM = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMemberAssigned().getId(), flightAssignment.getId()).stream().toList();
			for (Leg l : legByFCM)
				if (this.legIsCompatible(flightAssignment.getLegRelated(), l)) {
					legCompatible = false;
					super.state(legCompatible, "legRelated", "acme.validation.legCompatible.message");
					break;
				}
		}

		boolean confirmation;
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

		SelectChoices flightcrewsDuties;

		SelectChoices legChoices;
		List<Leg> legs;

		SelectChoices flightCrewMemberChoices;
		Collection<FlightCrewMember> availableFlightCrewMembers;

		flightcrewsDuties = SelectChoices.from(FlightCrewsDuty.class, flightAssignment.getFlightCrewsDuty());

		legs = this.repository.findAllLegs();
		legChoices = SelectChoices.from(legs, "label", flightAssignment.getLegRelated());

		availableFlightCrewMembers = this.repository.findAvailableFlightCrewMembers();
		flightCrewMemberChoices = SelectChoices.from(availableFlightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMemberAssigned());

		dataset = super.unbindObject(flightAssignment, "flightCrewsDuty", "lastUpdate", "remarks", "draftMode");
		dataset.put("currentStatus", CurrentStatus.PENDING);
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
