
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRepository;

@Validator
public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private BookingRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidBooking annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (booking == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		// 1. LocatorCode is unique?
		Booking existingBooking = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		boolean isUnique = existingBooking == null || existingBooking.equals(booking);
		super.state(context, isUnique, "locatorCode", "acme.validation.booking.duplicated-locator-code.message");

		// 2. If flight is assign, it is published?
		if (booking.getFlight() != null) {
			boolean validFlight = true;

			if (booking.getFlight().isDraftMode())
				validFlight = false;

			super.state(context, validFlight, "flight", "acme.validation.booking.invalid-flight.message");
		}

		// 3. Only in draftMode is allowed to have null flight value

		if (booking.getFlight() == null && !booking.isDraftMode())
			super.state(context, false, "flight", "acme.validation.booking.flight-required-when-publishing");

		result = !super.hasErrors(context);
		return result;
	}
}
