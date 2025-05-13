
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
import acme.entities.legs.Leg;
import acme.entities.recommendation.Recommendation;
import acme.features.administrator.recommendation.AdministratorRecommendationRepository;
import acme.features.customer.recommendation.RecommendationApiService;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository				repository;

	@Autowired
	private RecommendationApiService				recommendationService;

	@Autowired
	private AdministratorRecommendationRepository	recommendationRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && super.getRequest().getPrincipal().hasRealm(customer);

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

			// Añadir las recomendaciones según el destino
			if (booking.getFlight() != null) {
				List<Leg> orderedLegs = this.repository.findLegsByFlightId(booking.getFlight().getId()).stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());
				Leg lastLeg = orderedLegs.get(orderedLegs.size() - 1);

				String destinationCity = booking.getFlight().destinationCity();
				String destinationCountry = lastLeg.getArrivalAirport().getCountry();

				if (destinationCity != null && destinationCountry != null) {
					List<Recommendation> recommendations = this.recommendationService.fetchRecommendationsForCityAndCountry(destinationCity, destinationCountry);
					List<Recommendation> cityRecommendations = this.recommendationRepository.findByCity(destinationCity);

					for (Recommendation cityRec : cityRecommendations) {
						boolean alreadyExists = recommendations.stream().anyMatch(existing -> existing.getTitle().equals(cityRec.getTitle()));

						if (!alreadyExists)
							recommendations.add(cityRec);
					}

					StringBuilder recommendationsText = new StringBuilder();
					for (Recommendation rec : recommendations)
						recommendationsText.append(rec.getTitle()).append(" - ").append(rec.getCategory()).append("\n").append(rec.getEstimatedCost()).append("\n").append(rec.getDescription()).append("\n\n");

					dataset.put("recommendationsDisplay", recommendationsText.toString());
				}
			}
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
		dataset.put("bookingDraftMode", booking.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
