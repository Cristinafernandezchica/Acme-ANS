
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		FlightCrewMember fcmLogged;
		ActivityLog alSelected;

		boolean existingAL = false;
		boolean isFlightAssignmentOwner = false;
		boolean isPublished = true;
		boolean hasRegistrationMoment = true;

		String metodo = super.getRequest().getMethod();

		int fcmIdLogged = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			Integer alId = super.getRequest().getData("id", Integer.class);
			if (alId != null) {
				fcmLogged = this.repository.findFlighCrewMemberById(fcmIdLogged);
				List<ActivityLog> allFA = this.repository.findAllActivityLog();
				alSelected = this.repository.findActivityLogById(alId);
				existingAL = alSelected != null || allFA.contains(alSelected) && alSelected != null;
				hasRegistrationMoment = super.getRequest().hasData("registrationMoment");
				if (existingAL) {
					isFlightAssignmentOwner = alSelected.getFlightAssignmentRelated().getFlightCrewMemberAssigned() == fcmLogged;
					if (metodo.equals("GET"))
						isPublished = !alSelected.isDraftMode();
				}
			}
		}

		super.getResponse().setAuthorised(isFlightAssignmentOwner && isPublished && hasRegistrationMoment);
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
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.delete(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("faId", activityLog.getFlightAssignmentRelated().getId());

		super.getResponse().addData(dataset);
	}

}
