
package acme.features.flightCrewMember.activityLog;

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

		super.getResponse().setAuthorised(true);
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

		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
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
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("faId", activityLog.getFlightAssignmentRelated().getId());

		super.getResponse().addData(dataset);
	}

}
