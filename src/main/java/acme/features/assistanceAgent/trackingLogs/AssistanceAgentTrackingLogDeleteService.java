
package acme.features.assistanceAgent.trackingLogs;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		Claim claim;
		Integer id;
		TrackingLog trackingLog;
		AssistanceAgent assistanceAgent;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				trackingLog = this.repository.findTrackingLogById(id);
				claim = this.repository.findClaimByTrackingLogId(id);
				assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
				status = trackingLog != null && trackingLog.isDraftMode() && claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(assistanceAgent);
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "status");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		;
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statuses;
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution");

		statuses = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("statuses", statuses);
		dataset.put("claimId", trackingLog.getClaim().getId());
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
