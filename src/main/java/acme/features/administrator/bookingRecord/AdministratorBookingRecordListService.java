
package acme.features.administrator.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.bookingRecord.BookingRecord;

@GuiService
public class AdministratorBookingRecordListService extends AbstractGuiService<Administrator, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<BookingRecord> bookingRecord;
		Booking booking;

		booking = super.getRequest().getData("booking", Booking.class);

		bookingRecord = this.repository.findBookingRecordByBookingId(booking.getId());

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;

		dataset = super.unbindObject(bookingRecord, "booking.locatorCode", "passenger.fullName");

		super.getResponse().addData(dataset);

	}

}
