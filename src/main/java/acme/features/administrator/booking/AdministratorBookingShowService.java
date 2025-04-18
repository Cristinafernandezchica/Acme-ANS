
package acme.features.administrator.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && !booking.isDraftMode();

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
		Collection<Flight> flights;
		Collection<Flight> generalFlights;
		SelectChoices choices;
		SelectChoices generalChoices;
		SelectChoices classChoices;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());
		generalFlights = this.repository.findAllFlights();

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");

		if (!booking.isDraftMode()) {
			generalChoices = SelectChoices.from(generalFlights, "flightLabel", booking.getFlight());
			dataset.put("flight", generalChoices.getSelected() != null ? generalChoices.getSelected().getKey() : "0");
			dataset.put("flights", generalChoices);

		} else {
			// Si la booking está en draftmode, usa solo vuelos disponibles
			boolean flightStillValid = flights.contains(booking.getFlight());

			if (!flightStillValid)
				booking.setFlight(null); // Desasignar el vuelo si ya no es válido

			choices = SelectChoices.from(flights, "flightLabel", booking.getFlight());

			dataset.put("flight", booking.getFlight() != null && choices.getSelected() != null ? choices.getSelected().getKey() : "0");
			dataset.put("flights", choices);
		}

		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}
