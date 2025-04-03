
package acme.features.flightCrewMember.flightAssigment;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightCrewsDuty;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


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
		int id;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		FlightAssignment faBaseData;
		int id = super.getRequest().getData("id", int.class);
		faBaseData = this.repository.findFlightAssignmentById(id);

		// Comprobación de flightCrewsDuty no modificado
		boolean isOriginalFlightCrewsDuty;
		isOriginalFlightCrewsDuty = flightAssignment.getFlightCrewsDuty() == faBaseData.getFlightCrewsDuty();
		super.state(isOriginalFlightCrewsDuty, "flightCrewsDuty", "acme.validation.isOriginalFlightCrewsDuty.message");

		// Comprobación de lastUpdate no modificado
		boolean isOriginalLastUpdate;
		isOriginalLastUpdate = flightAssignment.getLastUpdate() == faBaseData.getLastUpdate();
		super.state(isOriginalLastUpdate, "lastUpdate", "acme.validation.isOriginalLastUpdate.message");

		// Comprobación de currentStatus no modificado
		boolean isOriginalCurrentStatus;
		isOriginalCurrentStatus = flightAssignment.getCurrentStatus() == faBaseData.getCurrentStatus();
		super.state(isOriginalCurrentStatus, "currentStatus", "acme.validation.isOriginalCurrentStatus.message");

		// Comprobación de remarks no modificado
		boolean isOriginalRemarks;
		isOriginalRemarks = flightAssignment.getRemarks().equals(faBaseData.getRemarks());
		super.state(isOriginalRemarks, "remarks", "acme.validation.isOriginalRemarks.message");

		// Comprobacion de leg no modificado
		boolean isOriginalLeg;
		isOriginalLeg = flightAssignment.getLegRelated() == faBaseData.getLegRelated();
		super.state(isOriginalLeg, "legRelated", "acme.validation.isOriginalLeg.message");

		// Comprobación de FCM no modificado
		boolean isOriginalFCM;
		isOriginalFCM = flightAssignment.getFlightCrewMemberAssigned() == faBaseData.getFlightCrewMemberAssigned();
		super.state(isOriginalFCM, "flightCrewMemberAssigned", "acme.validation.isOriginalFCM.message");

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
		dataset.put("faId", flightAssignment.getId());

		super.getResponse().addData(dataset);
	}

}
