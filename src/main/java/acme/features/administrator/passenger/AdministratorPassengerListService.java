
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@GuiService
public class AdministratorPassengerListService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Integer bookingId;
		Booking booking;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty()) {
			bookingId = super.getRequest().getData("bookingId", Integer.class);
			if (bookingId != null) {
				booking = this.repository.findBookingById(bookingId);
				status = booking != null;
			}
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int bookingId;
		Booking booking;

		if (super.getRequest().getData().isEmpty())
			passengers = List.of();
		else {
			bookingId = super.getRequest().getData("bookingId", int.class);
			booking = this.repository.findBookingById(bookingId);
			passengers = this.repository.findPassengersByBookingId(bookingId);
			super.getResponse().addGlobal("bookingId", bookingId);
			super.getResponse().addGlobal("bookingDraftMode", booking.isDraftMode());

		}
		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");

		super.getResponse().addData(dataset);
	}

}
