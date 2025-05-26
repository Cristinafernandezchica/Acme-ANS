
package acme.features.administrator.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flights.Flight;

@GuiService
public class AdministratorBookingShowService extends AbstractGuiService<Administrator, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		Integer bookingId;
		Booking booking;

		if (!super.getRequest().getData().isEmpty()) {
			bookingId = super.getRequest().getData("id", Integer.class);
			if (bookingId != null) {
				booking = this.repository.findBookingById(bookingId);
				status = booking != null && !booking.isDraftMode();
			}
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		this.getBuffer().addData(booking);
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> generalFlights;
		SelectChoices generalChoices;
		SelectChoices classChoices;
		Dataset dataset;

		generalFlights = this.repository.findAllFlights();

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");

		generalChoices = SelectChoices.from(generalFlights, "flightLabel", booking.getFlight());
		dataset.put("flight", generalChoices.getSelected() != null ? generalChoices.getSelected().getKey() : "0");
		dataset.put("flights", generalChoices);

		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}
