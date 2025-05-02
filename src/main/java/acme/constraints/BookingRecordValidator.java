
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
	public boolean isValid(final BookingRecord record, final ConstraintValidatorContext context) {
		assert context != null;

		if (record == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		if (record.getBooking() == null)
			super.state(context, false, "booking", "javax.validation.constraints.NotNull.message");

		if (record.getPassenger() == null)
			super.state(context, false, "passenger", "javax.validation.constraints.NotNull.message");

		if (record.getBooking() != null && record.getPassenger() != null) {
			boolean isUnique = this.repository.findByBookingAndPassenger(record.getBooking().getId(), record.getPassenger().getId()) == null;

			super.state(context, isUnique, "booking", "acme.validation.bookingRecord.duplicated.message");
		}

		return !super.hasErrors(context);
	}
}
