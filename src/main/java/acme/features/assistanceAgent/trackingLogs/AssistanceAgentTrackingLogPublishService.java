
package acme.features.assistanceAgent.trackingLogs;

import java.util.Arrays;
import java.util.Collection;

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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		boolean isCorrectStatus = true;
		String trackingLogStatus;
		boolean isCorrectClaim = false;
		Claim claim;
		int id;
		TrackingLog trackingLog;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			id = super.getRequest().getData("id", int.class);
			trackingLog = this.repository.findTrackingLogById(id);
			claim = this.repository.findClaimByTrackingLogId(id);

			isCorrectClaim = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && trackingLog != null && trackingLog.isDraftMode();
		}

		if (super.getRequest().getMethod().equals("POST")) {
			trackingLogStatus = super.getRequest().getData("status", String.class);

			if (!Arrays.toString(TrackingLogStatus.values()).concat("0").contains(trackingLogStatus) || trackingLogStatus == null)
				isCorrectStatus = false;
		}

		status = isCorrectClaim && isCorrectStatus;

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
		TrackingLogStatus status;
		int trackingLogId;
		TrackingLog oldTrackingLog;
		Double percentage;
		Double minPercentage;
		Collection<TrackingLog> trackingLogs;
		boolean isCorrectPercentage = true;
		boolean isCorrectPercentageStatus = true;
		boolean isPossibleToPublish = true;

		status = super.getRequest().getData("status", TrackingLogStatus.class);
		percentage = super.getRequest().getData("resolutionPercentage", Double.class);

		if (percentage.equals(100.00) && status.equals(TrackingLogStatus.PENDING) || !percentage.equals(100.00) && !status.equals(TrackingLogStatus.PENDING))
			isCorrectPercentageStatus = false;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLogs = this.repository.findTrackingLogsByClaimIdExcludingOne(trackingLog.getClaim().getId(), trackingLogId);
		oldTrackingLog = this.repository.findTrackingLogById(trackingLogId);

		if (!trackingLogs.isEmpty()) {
			minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
			Long maximumTrackingLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage().equals(100.00)).count();
			if (Long.valueOf(0).equals(maximumTrackingLogs))
				if (percentage.equals(100.00))
					isPossibleToPublish = trackingLogs.stream().allMatch(t -> !t.isDraftMode());
				else
					isCorrectPercentage = percentage > minPercentage || percentage.equals(oldTrackingLog.getResolutionPercentage());
			else if (Long.valueOf(1).equals(maximumTrackingLogs)) {
				TrackingLog maximumTrackingLog = trackingLogs.stream().findFirst().get();
				if (percentage.equals(100.00))
					isCorrectPercentage = !maximumTrackingLog.isDraftMode() && status.equals(maximumTrackingLog.getStatus());
				else
					isCorrectPercentage = percentage.equals(oldTrackingLog.getResolutionPercentage());
			} else if (Long.valueOf(2).equals(maximumTrackingLogs))
				isCorrectPercentage = percentage.equals(oldTrackingLog.getResolutionPercentage());
		}

		super.state(isCorrectPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
		super.state(isCorrectPercentageStatus, "status", "acme.validation.trackingLog.resolutionPercentageStatus.message");
		super.state(isPossibleToPublish, "resolutionPercentage", "acme.validation.trackingLog.publish.message");
		super.state(!trackingLog.getClaim().isDraftMode(), "draftMode", "acme.validation.claim.NoDraftMode.message");
		super.state(trackingLog.isDraftMode(), "draftMode", "acme.validation.trackingLog.draftMode.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
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
