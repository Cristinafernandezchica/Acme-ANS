
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		FlightCrewMember fcmLogged;
		ActivityLog alSelected;

		boolean existingAL = false;
		boolean isFlightAssignmentOwner = false;

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty()) {
			Integer alId = super.getRequest().getData("id", Integer.class);
			if (alId != null) {
				fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
				List<FlightAssignment> allFA = this.repository.findAllFlightAssignments();
				alSelected = this.repository.findActivityLogById(alId);
				existingAL = alSelected != null || allFA.contains(alSelected);
				if (alSelected != null)
					isFlightAssignmentOwner = alSelected.getFlightAssignmentRelated().getFlightCrewMemberAssigned() == fcmLogged;
			}
		}

		super.getResponse().setAuthorised(isFlightAssignmentOwner);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode", "flightAssignmentRelated");

		super.getResponse().addData(dataset);
	}

}
