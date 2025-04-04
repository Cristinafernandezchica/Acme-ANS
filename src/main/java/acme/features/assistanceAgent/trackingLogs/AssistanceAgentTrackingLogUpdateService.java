
package acme.features.assistanceAgent.trackingLogs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int id;
		TrackingLog trackingLog;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		claim = this.repository.findClaimByTrackingLogId(id);

		status = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && trackingLog != null && trackingLog.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;
		Date moment;

		moment = MomentHelper.getCurrentMoment();

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		trackingLog.setLastUpdateMoment(moment);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "status");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		Collection<TrackingLogStatus> statuses;
		TrackingLogStatus status;
		int trackingLogId;
		Double percentage;
		Double minPercentage;
		Collection<TrackingLog> trackingLogs;
		boolean isCorrectStatus;
		boolean isCorrectPercentage = true;
		boolean isCorrectPercentageStatus = true;

		statuses = Arrays.asList(TrackingLogStatus.values());
		status = super.getRequest().getData("status", TrackingLogStatus.class);
		isCorrectStatus = statuses.contains(status);

		percentage = super.getRequest().getData("resolutionPercentage", Double.class);
		if (percentage.equals(100.00) && status.equals(TrackingLogStatus.PENDING))
			isCorrectPercentageStatus = false;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLogs = this.repository.findTrackingLogsByClaimIdExcludingOne(trackingLog.getClaim().getId(), trackingLogId);

		if (!trackingLogs.isEmpty()) {
			minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
			Long maximumTrackingLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage().equals(100.00)).count();
			if (Long.valueOf(0).equals(maximumTrackingLogs))
				isCorrectPercentage = percentage > minPercentage;
			else if (Long.valueOf(1).equals(maximumTrackingLogs)) {
				TrackingLog maximumTrackingLog = trackingLogs.stream().findFirst().get();
				isCorrectPercentageStatus = percentage.equals(100.00) && status.equals(maximumTrackingLog.getStatus());
			} else if (Long.valueOf(2).equals(maximumTrackingLogs))
				isCorrectPercentage = false;
		}

		super.state(isCorrectPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
		super.state(isCorrectPercentageStatus, "status", "acme.validation.trackingLog.resolutionPercentageStatus.message");
		super.state(isCorrectStatus, "status", "acme.validation.trackingLog.status.message");
		super.state(!trackingLog.getClaim().isDraftMode(), "draftMode", "acme.validation.claim.draftMode.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
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
