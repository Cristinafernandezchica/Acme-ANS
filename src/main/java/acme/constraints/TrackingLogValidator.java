
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
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
		if (context == null)
			throw new IllegalArgumentException("ConstraintValidatorContext must not be null");

		boolean result = true;

		if (trackingLog == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("A Tracking Log can't be null").addConstraintViolation();
		} else if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && trackingLog.getResolutionPercentage() == 100.00) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("The status can be “PENDING” only when the resolution percentage is not 100%").addConstraintViolation();
		} else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && trackingLog.getResolutionPercentage() != 100.00) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("The status can be “ACCEPTED” or “REJECTED” only when the resolution percentage gets to 100%").addConstraintViolation();
		} else if (!trackingLog.getStatus().equals(TrackingLogStatus.PENDING) && (trackingLog.getResolution() == null || trackingLog.getResolution().isBlank())) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("If the status is not “PENDING”, then the resolution is mandatory").addConstraintViolation();
		}

		return result;
	}

}
