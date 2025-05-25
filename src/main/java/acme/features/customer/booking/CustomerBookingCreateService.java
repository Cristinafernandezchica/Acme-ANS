
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
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		boolean status = true;
		boolean noId = true;
		boolean fakeUpdate = true;

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				fakeUpdate = false;
		}

		status = fakeUpdate && noId;

		if (super.getRequest().getMethod().equals("POST")) {
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

		super.getResponse().setAuthorised(status);
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

			booking.setFlight(flight);
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

			existingCodes = new HashSet<>(this.repository.findAllLocatorCodes()); // Refrescar códigos eliminados
		}
	}

}
