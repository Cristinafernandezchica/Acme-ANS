
package acme.features.customer.booking;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Booking booking;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setCustomer(customer);

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

			if (flight == null && flightId != 0)
				throw new IllegalStateException("It is not possible to create a booking with this flight.");

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
		booking.setLocatorCode(this.generateLocatorCode());
		booking.setPurchaseMoment(moment);
		booking.setPrice(booking.getPrice());
		booking.setDraftMode(true);

	}

	@Override
	public void validate(final Booking booking) {
		Collection<Flight> validFlights = this.repository.findAllFlights().stream().filter(f -> f.getScheduledDeparture() != null && !f.isDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());
		if (!validFlights.contains(booking.getFlight()) && booking.getFlight() != null)
			throw new IllegalStateException("It is not possible to create a booking with this flight.");

		boolean isFlightValid = booking.getFlight() == null || validFlights.contains(booking.getFlight());
		super.state(isFlightValid, "flight", "acme.validation.booking.flight.message");

	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
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

		dataset.put("flight", choices.getSelected() != null && choices.getSelected().getKey() != null ? choices.getSelected().getKey() : "0");
		dataset.put("classes", classes);
		dataset.put("travelClass", classes.getSelected().getKey());

		super.getResponse().addData(dataset);

	}

	private String generateLocatorCode() {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Set<String> existingCodes = new HashSet<>(this.repository.findAllLocatorCodes());

		while (true) { // Si todos los códigos están ocupados, vuelve a iterar
			for (int letterCount = 1; letterCount <= 8; letterCount++) { // Usa de 1 a 8 letras
				int maxNumbers = (int) Math.pow(10, 8 - letterCount) - 1; // Ajusta los números

				for (int letterIndex = 0; letterIndex < Math.pow(letters.length(), letterCount); letterIndex++) {
					StringBuilder letterPart = new StringBuilder();
					int tempIndex = letterIndex;

					for (int i = 0; i < letterCount; i++) {
						letterPart.insert(0, letters.charAt(tempIndex % letters.length()));
						tempIndex /= letters.length();
					}

					for (int num = 0; num <= maxNumbers; num++) {
						String numberPart = String.format("%0" + (8 - letterCount) + "d", num);
						String locator = letterPart.toString() + numberPart;

						if (!existingCodes.contains(locator))
							return locator; // Devuelve el primer código disponible
					}
				}
			}

			// Si ha recorrido todo y no encontró un código libre, revisa si hay códigos eliminados
			if (existingCodes.size() >= Math.pow(26, 8))
				throw new IllegalStateException("No available locator codes.");

			existingCodes = new HashSet<>(this.repository.findAllLocatorCodes()); // Refrescar códigos eliminados
		}
	}

}
