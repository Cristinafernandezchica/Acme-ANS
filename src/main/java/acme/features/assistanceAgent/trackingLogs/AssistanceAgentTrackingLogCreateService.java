
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		boolean isCorrectClaim = false;
		boolean isCorrectStatus = true;
		boolean isFakeUpdate = true;
		boolean isCompletedClaim = true;
		String trackingLogStatus;
		Integer claimId;
		Claim claim;

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				isFakeUpdate = false;
		}

		if (super.getRequest().hasData("claimId")) {
			claimId = super.getRequest().getData("claimId", Integer.class);
			if (claimId != null) {
				claim = this.repository.findClaimById(claimId);
				isCorrectClaim = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

				Integer maximumTrackingLogs = this.repository.findTrackingLogs100PercentageByClaimId(claimId).size();
				if (maximumTrackingLogs >= 2)
					isCompletedClaim = false;
			}
		}

		if (super.getRequest().getMethod().equals("POST")) {
			trackingLogStatus = super.getRequest().getData("status", String.class);

			if (!Arrays.toString(TrackingLogStatus.values()).concat("0").contains(trackingLogStatus) || trackingLogStatus == null)
				isCorrectStatus = false;
		}

		status = isCorrectClaim && isFakeUpdate && isCompletedClaim && isCorrectStatus;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int claimId;
		Claim claim;
		Date moment;

		claimId = super.getRequest().getData("claimId", int.class);
		claim = this.repository.findClaimById(claimId);

		moment = MomentHelper.getCurrentMoment();

		trackingLog = new TrackingLog();
		trackingLog.setLastUpdateMoment(moment);
		trackingLog.setClaim(claim);
		trackingLog.setStatus(TrackingLogStatus.PENDING);
		trackingLog.setResolutionPercentage(0.00);
		trackingLog.setDraftMode(true);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "status");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		TrackingLogStatus status;
		int claimId;
		Double percentage;
		Double minPercentage;
		Collection<TrackingLog> trackingLogs;
		boolean isCorrectPercentage = true;
		boolean isPercentage100 = true;
		boolean isPreviousTrackingLogDraftMode = true;
		boolean isPreviousTrackingLogStatus = true;

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null) {
			percentage = super.getRequest().getData("resolutionPercentage", Double.class);
			status = super.getRequest().getData("status", TrackingLogStatus.class);
			claimId = super.getRequest().getData("claimId", int.class);
			trackingLogs = this.repository.findTrackingLogsByClaimId(claimId);

			if (!trackingLogs.isEmpty()) {
				minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
				Integer maximumTrackingLogs = this.repository.findTrackingLogs100PercentageByClaimId(claimId).size();
				if (maximumTrackingLogs.equals(0))
					isCorrectPercentage = percentage > minPercentage;
				else if (maximumTrackingLogs.equals(1)) {
					TrackingLog maximumTrackingLog = trackingLogs.stream().findFirst().get();
					if (!percentage.equals(100.00))
						isPercentage100 = false;
					else if (maximumTrackingLog.isDraftMode())
						isPreviousTrackingLogDraftMode = false;
					else if (!status.equals(maximumTrackingLog.getStatus()))
						isPreviousTrackingLogStatus = false;
				}
			}
		}

		super.state(isCorrectPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
		super.state(isPreviousTrackingLogDraftMode, "*", "acme.validation.trackingLog.previousDraftMode.message");
		super.state(isPercentage100, "resolutionPercentage", "acme.validation.trackingLog.percentage100.message");
		super.state(isPreviousTrackingLogStatus, "status", "acme.validation.trackingLog.previousStatus.message");
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
		dataset.put("claimId", super.getRequest().getData("claimId", int.class));
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
