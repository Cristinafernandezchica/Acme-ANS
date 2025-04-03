
package acme.features.manager.legs;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.Status;
import acme.entities.airline.Airline;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Leg leg;
		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		Airline airline = flight.getManager().getAirline();

		leg = new Leg();
		leg.setDraftMode(true);
		leg.setFlight(flight);
		leg.setFlightNumber(airline.getIataCode() + this.numberOfFlightNumber(airline));
		leg.setStatus(LegStatus.ON_TIME);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	private String numberOfFlightNumber(final Airline airline) {
		String newNumber = null;
		List<Integer> numbers = new ArrayList<>();
		Collection<String> airlineFlightNumbers = this.repository.findAllLegsFlightNumberByAirlineId(airline.getId());
		if (!airlineFlightNumbers.isEmpty()) {
			for (String flightNumber : airlineFlightNumbers) {
				String numberPart = flightNumber.substring(3);
				int number = Integer.parseInt(numberPart);
				numbers.add(number);
			}
			Collections.sort(numbers);
			int maxNumber = numbers.get(numbers.size() - 1);
			if (maxNumber == 9999) {
				for (int nextNumber = 0; nextNumber <= 9999; nextNumber++)
					if (!numbers.contains(nextNumber)) {
						newNumber = String.format("%04d", nextNumber);
						break;
					}
			} else {
				int nextNumber = maxNumber + 1;
				newNumber = String.format("%04d", nextNumber);
			}

		} else
			newNumber = String.format("%04d", 0);
		return newNumber;
	}

	@Override
	public void validate(final Leg leg) {

		if (leg.getScheduledDeparture() != null && MomentHelper.isBefore(leg.getScheduledDeparture(), MomentHelper.getCurrentMoment()))
			super.state(false, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.past");

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null && MomentHelper.isBefore(leg.getScheduledArrival(), leg.getScheduledDeparture()))
			super.state(false, "scheduledDeparture", "acme.validation.leg.departure.after.arrival.message");

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			Date departureWithDelta = MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 5, ChronoUnit.MINUTES);
			if (MomentHelper.isBefore(leg.getScheduledArrival(), MomentHelper.getCurrentMoment()))
				super.state(false, "scheduledArrival", "acme.validation.leg.scheduledArrival.past");

			if (MomentHelper.isBefore(leg.getScheduledArrival(), departureWithDelta))
				super.state(false, "scheduledArrival", "acme.validation.leg.departure.arrival.difference.message");
		}

		if (leg.getDepartureAirport() != null && leg.getDepartureAirport().equals(leg.getArrivalAirport())) {
			super.state(false, "arrivalAirport", "acme.validation.leg.same.departure.arrival.airport");
			super.state(false, "departureAirport", "acme.validation.leg.same.departure.arrival.airport");
		}

		Airline airline = leg.getFlight().getManager().getAirline();
		if (leg.getFlightNumber().length() == 7 && !leg.getFlightNumber().substring(0, 3).equals(airline.getIataCode()))
			super.state(false, "flightNumber", "acme.validation.leg.invalid.iata.flightNumber");

		if (!leg.isDraftMode())
			super.state(false, "*", "acme.validation.leg.create.no.draftmode");

		if (leg.getStatus() != LegStatus.ON_TIME)
			super.state(false, "status", "acme.validation.leg.status.always.ontime");

		if (!leg.getFlight().isDraftMode())
			super.state(false, "*", "acme.validation.leg.create.no.flight.draftmode");

		if (leg.getAircraft() != null) {
			boolean operativeAircraft = leg.getAircraft().getStatus().equals(Status.ACTIVE_SERVICE);
			super.state(operativeAircraft, "aircraft", "acme.validation.leg.operative.aircraft.message");
		}

	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		// SelectChoices statuses;
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		SelectChoices selectedAircrafts;
		Collection<Aircraft> activeAircrafts;
		Collection<Airport> airports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;

		// statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircrafts = this.repository.findAllAircraftsByAirlineId(leg.getFlight().getAirline().getId());
		activeAircrafts = aircrafts.stream().filter(a -> a.getStatus().equals(Status.ACTIVE_SERVICE)).collect(Collectors.toList());
		selectedAircrafts = SelectChoices.from(activeAircrafts, "model", leg.getAircraft());

		airports = this.repository.findAllAirports();
		selectedDepartureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		selectedArrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "draftMode");
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("status", LegStatus.ON_TIME);
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("departureAirports", selectedDepartureAirport);
		dataset.put("arrivalAirports", selectedArrivalAirport);
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("aircrafts", selectedAircrafts);
		if (leg.getScheduledDeparture() != null)
			dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
