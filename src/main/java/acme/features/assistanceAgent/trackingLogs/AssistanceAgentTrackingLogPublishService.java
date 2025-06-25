
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
		boolean isCorrectTrackingLog = false;
		Claim claim;
		AssistanceAgent assistanceAgent;
		Integer id;
		TrackingLog trackingLog;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				trackingLog = this.repository.findTrackingLogById(id);
				claim = this.repository.findClaimByTrackingLogId(id);
				assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
				isCorrectTrackingLog = trackingLog != null && trackingLog.isDraftMode() && claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(assistanceAgent);
			}
		}

		if (super.getRequest().getMethod().equals("POST")) {
			trackingLogStatus = super.getRequest().getData("status", String.class);

			if (!Arrays.toString(TrackingLogStatus.values()).concat("0").contains(trackingLogStatus) || trackingLogStatus == null)
				isCorrectStatus = false;
		}

		status = isCorrectTrackingLog && isCorrectStatus;

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
		boolean isPossibleToPublish = true;
		boolean isPercentage100 = true;
		boolean isPreviousTrackingLogDraftMode = true;
		boolean isPreviousTrackingLogStatus = true;

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null) {
			status = super.getRequest().getData("status", TrackingLogStatus.class);
			percentage = super.getRequest().getData("resolutionPercentage", Double.class);
			trackingLogId = super.getRequest().getData("id", int.class);
			trackingLogs = this.repository.findTrackingLogsByClaimIdExcludingOne(trackingLog.getClaim().getId(), trackingLogId);
			oldTrackingLog = this.repository.findTrackingLogById(trackingLogId);

			if (!trackingLogs.isEmpty()) {
				minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
				Integer maximumTrackingLogs = this.repository.findTrackingLogs100PercentageByClaimIdExcludingOne(trackingLog.getClaim().getId(), trackingLogId).size();
				if (maximumTrackingLogs.equals(0))
					if (percentage.equals(100.00))
						isPossibleToPublish = trackingLogs.stream().allMatch(t -> !t.isDraftMode());
					else
						isCorrectPercentage = percentage > minPercentage || percentage.equals(oldTrackingLog.getResolutionPercentage());
				else if (maximumTrackingLogs.equals(1)) {
					TrackingLog maximumTrackingLog = trackingLogs.stream().findFirst().get();
					if (!percentage.equals(100.00))
						isPercentage100 = percentage.equals(oldTrackingLog.getResolutionPercentage());
					else if (maximumTrackingLog.isDraftMode())
						isPreviousTrackingLogDraftMode = false;
					else if (!status.equals(maximumTrackingLog.getStatus()))
						isPreviousTrackingLogStatus = false;
				}
			}
		}

		super.state(isCorrectPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
		super.state(isPossibleToPublish, "resolutionPercentage", "acme.validation.trackingLog.publish.message");
		super.state(isPreviousTrackingLogDraftMode, "*", "acme.validation.trackingLog.previousDraftMode.message");
		super.state(isPercentage100, "resolutionPercentage", "acme.validation.trackingLog.percentage100.message");
		super.state(isPreviousTrackingLogStatus, "status", "acme.validation.trackingLog.previousStatus.message");
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
