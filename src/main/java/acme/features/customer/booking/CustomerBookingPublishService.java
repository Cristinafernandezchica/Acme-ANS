
package acme.features.customer.booking;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flights.Flight;
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		Customer customer;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			bookingId = super.getRequest().getData("id", int.class);
			booking = this.repository.findBookingById(bookingId);
			customer = booking == null ? null : booking.getCustomer();
			status = booking != null && super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		boolean hasFlightParam = super.getRequest().getData().containsKey("flight");

		if (hasFlightParam) {
			flightId = super.getRequest().getData("flight", int.class);
			flight = this.repository.findFlightById(flightId);

			if (flight == null && flightId != 0)
				throw new IllegalStateException("It is not possible to publish a booking with this flight.");

			booking.setFlight(flight);
		} else
			booking.setFlight(null);

		String rawTravelClass = super.getRequest().getData("travelClass", String.class);

		if (rawTravelClass != null && !rawTravelClass.trim().isEmpty() && !rawTravelClass.equals("0")) {
			boolean travelClassValid = Arrays.stream(TravelClass.values()).anyMatch(tc -> tc.name().equals(rawTravelClass));

			if (!travelClassValid)
				throw new IllegalStateException("Travel class selected is not valid");
		}

		super.bindObject(booking, "travelClass", "lastCardNibble");

	}

	@Override
	public void validate(final Booking booking) {
		Collection<Flight> validFlights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		if (!validFlights.contains(booking.getFlight()) && booking.getFlight() != null)
			throw new IllegalStateException("It is not possible to publish a booking with this flight.");

		boolean isFlightValid = booking.getFlight() != null && validFlights.contains(booking.getFlight());
		super.state(isFlightValid, "flight", "acme.validation.booking.flight.message");

		Collection<Passenger> passengers = this.repository.findPassengersByBookingId(booking.getId());
		boolean hasPassengersInDraftModeOrEmpty = passengers.isEmpty() || passengers.stream().anyMatch(Passenger::isDraftMode);
		super.state(!hasPassengersInDraftModeOrEmpty, "*", "acme.validation.booking.passengers.message");

		boolean hasCardNibble = booking.getLastCardNibble() != null && !booking.getLastCardNibble().trim().isEmpty();
		super.state(hasCardNibble, "lastCardNibble", "acme.validation.lastCardNibble.message");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		SelectChoices choices;
		SelectChoices classChoices;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		boolean flightStillValid = flights.contains(booking.getFlight());
		if (!flightStillValid)
			booking.setFlight(null);

		choices = SelectChoices.from(flights, "flightLabel", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");

		dataset.put("flight", booking.getFlight() != null && choices.getSelected() != null ? choices.getSelected().getKey() : "0");
		dataset.put("flights", choices);
		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());
		dataset.put("bookingDraftMode", booking.isDraftMode());

		super.getResponse().addData(dataset);

	}

}
