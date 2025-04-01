
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean authorization = true;
		int fcmIdLogged;
		FlightCrewMember fcmLogged;
		Collection<FlightAssignment> flightAssignments;

		fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignments = this.repository.findFlightAssignmentsByFCMId(fcmIdLogged);
		fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);

		for (FlightAssignment fa : flightAssignments) {
			authorization = fa.getFlightCrewMemberAssigned().equals(fcmLogged);
			if (!authorization)
				break;
		}

		super.getResponse().setAuthorised(authorization);
	}

	@Override
	public void load() {
		int faId = super.getRequest().getData("faId", int.class);
		FlightAssignment flightAssignmentRelated = this.repository.findFlightAssignmentById(faId);

		ActivityLog activityLog = new ActivityLog();
		activityLog.setDraftMode(true);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setFlightAssignmentRelated(flightAssignmentRelated);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "flightAssignmentRelated");

	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = null;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode", "flightAssignmentRelated");

		super.getResponse().addData(dataset);
	}

}
