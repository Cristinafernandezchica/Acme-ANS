
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private ActivityLogRepository repository;


	// AbstractGuiService interface -----------------------------------------------------
	@Override
	public void authorise() {
		boolean isFlightAssignmentOwner;

		FlightCrewMember fcmLogged;
		FlightAssignment faSelected;
		int faId = super.getRequest().getData("faId", int.class);

		// Comprobación de que la flight assignments relacionadas al activityLog sean asiganadas al fcm
		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
		faSelected = this.repository.findFlightAssignmentById(faId);
		isFlightAssignmentOwner = faSelected.getFlightCrewMemberAssigned() == fcmLogged;

		super.getResponse().setAuthorised(isFlightAssignmentOwner);

	}

	@Override
	public void load() {
		Collection<ActivityLog> activityLogs;
		int faId = super.getRequest().getData("faId", int.class);
		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		activityLogs = this.repository.findOwnedActivityLogsByFAId(faId, fcmIdLogged);

		super.getResponse().addGlobal("faId", faId); // para el create

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");

	}

	@Override
	public void validate(final ActivityLog activityLog) {

	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "typeOfIncident", "description", "severityLevel");
		dataset.put("faId", activityLog.getFlightAssignmentRelated().getId());

		super.getResponse().addData(dataset);
	}

}
