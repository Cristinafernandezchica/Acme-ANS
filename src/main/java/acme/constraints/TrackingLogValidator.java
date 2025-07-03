
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
			super.state(context, false, "claim", "acme.validation.trackingLog.noDraftMode.claim");
			result = !super.hasErrors(context);
		} else if (trackingLog.getClaim().getRegistrationMoment().after(trackingLog.getLastUpdateMoment())) {
			super.state(context, false, "lastUpdateMoment", "acme.validation.trackingLog.incorrect.lastUpdateMoment");
			result = !super.hasErrors(context);
		} else {
			if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null)
				if (trackingLog.getResolutionPercentage() < 100.00) {
					if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
						super.state(context, false, "status", "acme.validation.trackingLog.noPending.status");
				} else if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
					super.state(context, false, "status", "acme.validation.trackingLog.pending.status");
				else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && (trackingLog.getResolution() == null || trackingLog.getResolution().isBlank() || StringHelper.isEqual("", trackingLog.getResolution(), true)))
					super.state(context, false, "resolution", "acme.validation.trackingLog.mandatory.resolution");
			result = !super.hasErrors(context);
		}
		return result;
	}

}
