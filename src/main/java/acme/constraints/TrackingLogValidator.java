
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (trackingLog == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			result = !super.hasErrors(context);
		} else if (trackingLog.getClaim().isDraftMode()) {
			super.state(context, false, "claim", "We cannot associate a tracking log with a claim in draft mode.");
			result = !super.hasErrors(context);
		} else if (trackingLog.getClaim().getRegistrationMoment().after(trackingLog.getLastUpdateMoment())) {
			super.state(context, false, "lastUpdateMoment", "We cannot associate a tracking log with a claim whose registration moment is later than the tracking log update moment.");
			result = !super.hasErrors(context);
		} else {
			if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null)
				if (trackingLog.getResolutionPercentage() < 100.00) {
					if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
						super.state(context, false, "status", "The status can be “ACCEPTED” or “REJECTED” only when the resolution percentage gets to 100%");
				} else if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
					super.state(context, false, "status", "The status can be “PENDING” only when the resolution percentage is not 100%");
				else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && (trackingLog.getResolution() == null || trackingLog.getResolution().isBlank() || StringHelper.isEqual("", trackingLog.getResolution(), true)))
					super.state(context, false, "resolution", "If the status is not “PENDING”, then the resolution is mandatory");
			result = !super.hasErrors(context);
		}
		return result;
	}

}
