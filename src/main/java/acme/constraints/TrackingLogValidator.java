
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result = true;
		if (trackingLog == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("A Tracking Log can't be a null").addConstraintViolation();
		} else if (this.repository.findClaimIndicator(trackingLog.getId()) != null && trackingLog.getResolution() == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("If a claim is accepted or rejected, the system must store its resolution").addConstraintViolation();
		}
		return result;
	}

}
