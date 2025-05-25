
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.bookingRecord.BookingRecord;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		Customer customer;
		boolean status = false;
		boolean fakeUpdate = true;

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				fakeUpdate = false;
		}

		if (super.getRequest().hasData("bookingId")) {
			bookingId = super.getRequest().getData("bookingId", int.class);
			booking = this.repository.findBookingById(bookingId);
			customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

			if (booking != null && booking.getCustomer().equals(customer) && booking.isDraftMode()) {
				status = true;

				if (super.getRequest().getMethod().equals("POST")) {
					String passengerRaw = super.getRequest().getData("passenger", String.class);

					if (passengerRaw == null || passengerRaw.trim().isEmpty())
						status = false;
					else {
						int pId = Integer.parseInt(passengerRaw);
						Passenger passenger = this.repository.findPassengerById(pId);
						Collection<Passenger> availablePassengers = this.repository.findAvailablePassengersByBookingId(customer.getId(), booking.getId());

						if (passenger == null && pId != 0 || passenger != null && !availablePassengers.contains(passenger))
							status = false;
					}
				}
			}
		}

		super.getResponse().setAuthorised(fakeUpdate && status);
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		int passengerId;
		Passenger passenger;

		boolean hasPassengerParam = super.getRequest().getData().containsKey("passenger");

		if (hasPassengerParam) {
			passengerId = super.getRequest().getData("passenger", int.class);
			passenger = this.repository.findPassengerById(passengerId);

			bookingRecord.setPassenger(passenger);
		}

	}

	@Override
	public void validate(final BookingRecord bookingRecord) {

		Booking booking = bookingRecord.getBooking();
		boolean notPublished = booking == null || booking.isDraftMode();
		super.state(notPublished, "booking", "acme.validation.bookingRecord.invalid-booking-publish.message");
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Passenger> availablePassengers;
		SelectChoices choises;
		Dataset dataset;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		availablePassengers = this.repository.findAvailablePassengersByBookingId(customer.getId(), bookingRecord.getBooking().getId());
		choises = SelectChoices.from(availablePassengers, "passportNumber", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord);
		dataset.put("booking", bookingRecord.getBooking());
		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("bookingDraftMode", bookingRecord.getBooking().isDraftMode());
		dataset.put("passengers", choises);
		dataset.put("passenger", choises.getSelected() != null && choises.getSelected().getKey() != null ? choises.getSelected().getKey() : "0");

		super.getResponse().addData(dataset);

	}

}
