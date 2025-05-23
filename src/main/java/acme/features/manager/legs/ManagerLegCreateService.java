
package acme.features.manager.legs;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

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
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {

		Integer flightId;
		Flight flight;
		Manager manager;
		boolean status = false;
		Airport depAirport = null;
		Airport arrAirport = null;
		Aircraft validAircraft = null;
		String legStatus;
		boolean existingLeg = true;

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (!super.getRequest().getData().isEmpty()) {
			flightId = super.getRequest().getData("flightId", Integer.class);
			if (flightId != null) {
				flight = this.repository.findFlightById(flightId);
				if (flight != null && flight.getManager().equals(manager))
					status = true;

				if (flight != null && !flight.isDraftMode())
					status = false;
				// Validaciones
				if (super.getRequest().getMethod().equals("POST")) {
					Integer legId = super.getRequest().getData("id", Integer.class);
					Leg leg = this.repository.findLegById(legId);
					if (leg != null)
						existingLeg = false;

					// Departure airport and arrival airport
					Integer airDepId = super.getRequest().getData("departureAirport", Integer.class);
					Integer airArrId = super.getRequest().getData("arrivalAirport", Integer.class);
					if (airDepId != null) {
						depAirport = this.repository.findAirportById(airDepId);
						if (airDepId == 0)
							status &= true;
						else if (depAirport == null)
							status &= false;
					} else
						status &= false;

					if (airArrId != null) {
						arrAirport = this.repository.findAirportById(airArrId);
						if (airArrId == 0)
							status &= true;
						else if (arrAirport == null)
							status &= false;
					} else
						status &= false;

					// Aircraft null
					Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
					if (aircraftId != null) {
						validAircraft = this.repository.findAircraftById(aircraftId);
						if (aircraftId == 0)
							status &= true;
						else if (validAircraft == null)
							status &= false;
					} else
						status &= false;

					// Status different of ON_TIME
					legStatus = super.getRequest().getData("status", String.class);
					if (legStatus != null && !legStatus.equals(LegStatus.ON_TIME.toString()))
						status = false;

				}
			}
		}
		super.getResponse().setAuthorised(status && existingLeg);

	}

	@Override
	public void load() {
		Leg leg;
		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		leg = new Leg();
		leg.setDraftMode(true);
		leg.setFlight(flight);
		leg.setStatus(LegStatus.ON_TIME);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
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

		if (leg.getAircraft() != null) {
			boolean operativeAircraft = leg.getAircraft().getStatus().equals(Status.ACTIVE_SERVICE);
			super.state(operativeAircraft, "aircraft", "acme.validation.leg.operative.aircraft.message");

			Airline airline = leg.getAircraft().getAirline();
			if (leg.getFlightNumber().length() == 7 && !leg.getFlightNumber().substring(0, 3).equals(airline.getIataCode())) {
				super.state(false, "flightNumber", "acme.validation.leg.invalid.iata.flightNumber");
				super.state(false, "flightNumber", "The airline's IATA code: " + airline.getIataCode());
			}
		}

		if (!leg.isDraftMode())
			super.state(false, "*", "acme.validation.leg.create.no.draftmode");

		if (leg.getFlight() != null && !leg.getFlight().isDraftMode())
			super.state(false, "*", "acme.validation.leg.create.no.flight.draftmode");

	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		SelectChoices selectedAircrafts;
		Collection<Airport> airports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;

		aircrafts = this.repository.findAllAircrafts();
		selectedAircrafts = SelectChoices.from(aircrafts, "aircraftLabel", leg.getAircraft());

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
