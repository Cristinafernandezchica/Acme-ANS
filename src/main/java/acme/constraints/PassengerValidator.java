
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.passenger.Passenger;
import acme.features.customer.passenger.CustomerPassengerRepository;

@Validator
public class PassengerValidator extends AbstractValidator<ValidPassenger, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	protected void initialise(final ValidPassenger annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {
		assert context != null;

		if (passenger == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		// 1. Is passportNumber unique per Customer?
		if (passenger.getCustomer() != null && passenger.getPassportNumber() != null && passenger.getId() != 0) {
			boolean existsDuplicate = this.repository.existsAnotherPassengerWithSamePassport(passenger.getPassportNumber(), passenger.getCustomer().getId(), passenger.getId());

			super.state(context, !existsDuplicate, "passportNumber", "acme.validation.passenger.duplicate-passport");
		}

		// 2. Is dateOfBirth a past date and valid? 
		if (passenger.getDateOfBirth() != null) {
			Date now = MomentHelper.getCurrentMoment();
			Date minDate = MomentHelper.parse("2000/01/01 00:00", "yyyy/MM/dd HH:mm");

			boolean inRange = !passenger.getDateOfBirth().before(minDate) && !passenger.getDateOfBirth().after(now);
			super.state(context, inRange, "dateOfBirth", "acme.validation.passenger.dateOfBirth.message");
		}

		return !super.hasErrors(context);
	}
}
