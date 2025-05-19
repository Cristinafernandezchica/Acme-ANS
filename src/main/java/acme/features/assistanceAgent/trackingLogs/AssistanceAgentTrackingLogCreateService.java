
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
		boolean isCorrectClaim;
		boolean isCorrectStatus = true;
		String trackingLogStatus;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("claimId", int.class);
		claim = this.repository.findClaimById(claimId);
		isCorrectClaim = claim != null && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

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
		boolean isCorrectPercentageStatus = true;

		status = super.getRequest().getData("status", TrackingLogStatus.class);
		percentage = super.getRequest().getData("resolutionPercentage", Double.class);

		if (percentage.equals(100.00) && status.equals(TrackingLogStatus.PENDING) || !percentage.equals(100.00) && !status.equals(TrackingLogStatus.PENDING))
			isCorrectPercentageStatus = false;

		claimId = super.getRequest().getData("claimId", int.class);
		trackingLogs = this.repository.findTrackingLogsByClaimId(claimId);

		if (!trackingLogs.isEmpty()) {
			minPercentage = trackingLogs.stream().findFirst().map(t -> t.getResolutionPercentage()).orElse(0.00);
			Long maximumTrackingLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage().equals(100.00)).count();
			if (Long.valueOf(0).equals(maximumTrackingLogs))
				isCorrectPercentage = percentage > minPercentage;
			else if (Long.valueOf(1).equals(maximumTrackingLogs)) {
				TrackingLog maximumTrackingLog = trackingLogs.stream().findFirst().get();
				isCorrectPercentage = !maximumTrackingLog.isDraftMode() && percentage.equals(100.00) && status.equals(maximumTrackingLog.getStatus());
			} else if (Long.valueOf(2).equals(maximumTrackingLogs))
				isCorrectPercentage = false;
		}

		super.state(isCorrectPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");
		super.state(isCorrectPercentageStatus, "status", "acme.validation.trackingLog.resolutionPercentageStatus.message");
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
		dataset.put("claimId", super.getRequest().getData("claimId", int.class));
		dataset.put("draftMode", trackingLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
