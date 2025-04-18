
package acme.features.administrator.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.bookingRecord.BookingRecord;
import acme.entities.passenger.Passenger;

@GuiService
public class AdministratorBookingRecordShowService extends AbstractGuiService<Administrator, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingRecordId;
		BookingRecord bookingRecord;
		boolean status;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);
		status = bookingRecord != null && !bookingRecord.getBooking().isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingRecordId;
		BookingRecord bookingRecord;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Booking> bookings;
		Collection<Passenger> passengers;
		SelectChoices classes;
		SelectChoices choises;
		Dataset dataset;

		bookings = this.repository.findAllBookings();
		passengers = this.repository.findAllPassengers();
		classes = SelectChoices.from(bookings, "locatorCode", bookingRecord.getBooking());
		choises = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());
		dataset = super.unbindObject(bookingRecord, "booking", "passenger", "draftMode");
		dataset.put("bookings", classes);
		dataset.put("booking", classes.getSelected().getKey());
		dataset.put("passengers", choises);
		dataset.put("passenger", choises.getSelected().getKey());

		super.getResponse().addData(dataset);

	}
}
