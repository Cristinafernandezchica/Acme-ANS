
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.bookingRecord.BookingRecord;
import acme.features.customer.bookingRecord.CustomerBookingRecordRepository;

@Validator
public class BookingRecordValidator extends AbstractValidator<ValidBookingRecord, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	protected void initialise(final ValidBookingRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final BookingRecord bookingRecord, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (bookingRecord == null || bookingRecord.getBooking() == null || bookingRecord.getPassenger() == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			super.state(context, false, "booking", "javax.validation.constraints.NotNull.message");
			super.state(context, false, "passenger", "javax.validation.constraints.NotNull.message");
		} else {
			boolean uniqueBookingRecord;
			BookingRecord existingBookingRecord;

			existingBookingRecord = this.repository.findByBookingAndPassenger(bookingRecord.getBooking().getId(), bookingRecord.getPassenger().getId());

			uniqueBookingRecord = existingBookingRecord == null || existingBookingRecord.equals(bookingRecord);

			super.state(context, uniqueBookingRecord, "booking", "acme.validation.bookingRecord.duplicated.message");

		}

		result = !super.hasErrors(context);

		return result;
	}
}
