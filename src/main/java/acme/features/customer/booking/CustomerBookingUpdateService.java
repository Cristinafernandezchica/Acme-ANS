
package acme.features.customer.booking;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		int bookingId;
		int flightId;
		Integer masterId;
		Customer customer;
		Booking booking;
		Flight flight;

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				bookingId = super.getRequest().getData("id", int.class);
				booking = this.repository.findBookingById(bookingId);
				customer = booking == null ? null : booking.getCustomer();
				status = super.getRequest().getPrincipal().hasRealm(customer) && booking != null && booking.isDraftMode();

				boolean hasFlightParam = super.getRequest().getData().containsKey("flight");
				if (hasFlightParam) {
					flightId = super.getRequest().getData("flight", int.class);
					flight = this.repository.findFlightById(flightId);
					Collection<Flight> validFlights = this.repository.findAllFlights().stream().filter(f -> f.getScheduledDeparture() != null && !f.isDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
						&& this.repository.findLegsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

					if (flight == null && flightId != 0)
						status = false;

					if (!validFlights.contains(flight) && flight != null)
						status = false;
				}

				if (super.getRequest().hasData("travelClass")) {
					String rawTravelClass = super.getRequest().getData("travelClass", String.class);

					if (rawTravelClass != null && !rawTravelClass.trim().isEmpty() && !rawTravelClass.equals("0")) {
						boolean travelClassValid = Arrays.stream(TravelClass.values()).anyMatch(tc -> tc.name().equals(rawTravelClass));

						if (!travelClassValid)
							status = false;
					}
				}
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

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;
		Date moment;

		moment = MomentHelper.getCurrentMoment();
		boolean hasFlightParam = super.getRequest().getData().containsKey("flight");

		if (hasFlightParam) {
			flightId = super.getRequest().getData("flight", int.class);
			flight = this.repository.findFlightById(flightId);

			booking.setFlight(flight);
		}

		super.bindObject(booking, "locatorCode", "travelClass", "lastCardNibble");
		booking.setPurchaseMoment(moment);
		booking.setPrice(booking.getPrice());
		booking.setDraftMode(true);
	}

	@Override
	public void validate(final Booking booking) {
		Collection<Flight> validFlights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		boolean isFlightValid = booking.getFlight() == null || validFlights.contains(booking.getFlight());
		super.state(isFlightValid, "flight", "acme.validation.booking.flight.message");
	}

	@Override
	public void perform(final Booking booking) {
		Booking persistentBooking = this.repository.findBookingById(booking.getId());
		if (persistentBooking != null) {
			persistentBooking.setFlight(booking.getFlight());
			persistentBooking.setTravelClass(booking.getTravelClass());
			persistentBooking.setLastCardNibble(booking.getLastCardNibble());
			persistentBooking.setPurchaseMoment(MomentHelper.getCurrentMoment());
			persistentBooking.setDraftMode(true);

			this.repository.save(persistentBooking);
		}
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		SelectChoices choices;
		SelectChoices classes;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		choices = SelectChoices.from(flights, "flightLabel", booking.getFlight());
		classes = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");
		dataset.put("flights", choices);
		dataset.put("flight", booking.getFlight() != null ? choices.getSelected().getKey() : "0");
		dataset.put("classes", classes);
		dataset.put("travelClass", classes.getSelected().getKey());
		dataset.put("bookingId", booking.getId());
		dataset.put("bookingDraftMode", booking.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
