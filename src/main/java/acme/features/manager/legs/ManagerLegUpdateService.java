
package acme.features.manager.legs;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Integer masterId;
		Leg leg;
		Manager manager;
		Airport depAirport = null;
		Airport arrAirport = null;
		Aircraft validAircraft = null;
		String legStatus;
		List<String> posibleLegStatus = new ArrayList<>();

		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				leg = this.repository.findLegById(masterId);
				boolean fN = super.getRequest().hasData("flightNumber");
				manager = leg == null ? null : leg.getFlight().getManager();
				status = leg != null && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId() && fN;

				if (super.getRequest().getMethod().equals("POST")) {
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

					// Validate status based on draftMode
					if (leg != null)
						if (leg.isDraftMode()) {
							legStatus = super.getRequest().getData("status", String.class);
							if (legStatus != null && !legStatus.equals(LegStatus.ON_TIME.toString()))
								status = false;
						} else {
							legStatus = super.getRequest().getData("status", String.class);
							posibleLegStatus.add(LegStatus.ON_TIME.toString());
							posibleLegStatus.add(LegStatus.DELAYED.toString());
							posibleLegStatus.add(LegStatus.CANCELLED.toString());
							posibleLegStatus.add(LegStatus.LANDED.toString());
							if (!legStatus.equals("0") && !posibleLegStatus.contains(legStatus))
								status = false;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
							String newDepartureDate = super.getRequest().getData("scheduledDeparture", String.class);
							String newArrivalDate = super.getRequest().getData("scheduledArrival", String.class);
							String newFlightNumber = super.getRequest().getData("flightNumber", String.class);
							Airport newDepartureAirport = super.getRequest().getData("departureAirport", Airport.class);
							Airport newArrivalAirport = super.getRequest().getData("arrivalAirport", Airport.class);
							Aircraft newAircraft = super.getRequest().getData("aircraft", Aircraft.class);

							Leg originalLeg = this.repository.findLegById(leg.getId());
							String originalDeparture = sdf.format(originalLeg.getScheduledDeparture());
							String originalArrival = sdf.format(originalLeg.getScheduledArrival());

							if (!originalDeparture.equals(newDepartureDate) || !originalArrival.equals(newArrivalDate) || !originalLeg.getFlightNumber().equals(newFlightNumber) || !originalLeg.getDepartureAirport().equals(newDepartureAirport)
								|| !originalLeg.getArrivalAirport().equals(newArrivalAirport) || !originalLeg.getAircraft().equals(newAircraft))
								status = false;
						}
				}
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {
		final boolean isDraftMode = leg.isDraftMode();
		final Date currentMoment = MomentHelper.getCurrentMoment();
		final Date scheduledDeparture = leg.getScheduledDeparture();
		final Date scheduledArrival = leg.getScheduledArrival();

		// Validaciones de tiempo
		if (scheduledDeparture != null) {
			if (isDraftMode && MomentHelper.isBefore(scheduledDeparture, currentMoment))
				super.state(false, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.past");

			if (scheduledArrival != null) {
				if (MomentHelper.isBefore(scheduledArrival, scheduledDeparture))
					super.state(false, "scheduledDeparture", "acme.validation.leg.departure.after.arrival.message");

				if (isDraftMode && MomentHelper.isBefore(scheduledArrival, currentMoment))
					super.state(false, "scheduledArrival", "acme.validation.leg.scheduledArrival.past");

				Date departureWithDelta = MomentHelper.deltaFromMoment(scheduledDeparture, 5, ChronoUnit.MINUTES);
				if (MomentHelper.isBefore(scheduledArrival, departureWithDelta))
					super.state(false, "scheduledArrival", "acme.validation.leg.departure.arrival.difference.message");
			}
		}

		// Validación de aeropuertos
		final Airport departureAirport = leg.getDepartureAirport();
		final Airport arrivalAirport = leg.getArrivalAirport();
		if (departureAirport != null && departureAirport.equals(arrivalAirport)) {
			super.state(false, "arrivalAirport", "acme.validation.leg.same.departure.arrival.airport");
			super.state(false, "departureAirport", "acme.validation.leg.same.departure.arrival.airport");
		}

		// Validación de aeronave
		final Aircraft aircraft = leg.getAircraft();
		if (aircraft != null) {
			super.state(aircraft.getStatus().equals(Status.ACTIVE_SERVICE), "aircraft", "acme.validation.leg.operative.aircraft.message");

			if (isDraftMode) {
				Airline airline = aircraft.getAirline();
				String flightNumber = leg.getFlightNumber();
				if (flightNumber.length() == 7 && !flightNumber.substring(0, 3).equals(airline.getIataCode())) {
					super.state(false, "flightNumber", "acme.validation.leg.invalid.iata.flightNumber");
					super.state(false, "flightNumber", "The airline's IATA code: " + airline.getIataCode());
				}
			}
		}

		// Validación de estado para vuelos publicados
		if (!isDraftMode && leg.getFlight().isDraftMode()) {
			LegStatus currentStatus = this.repository.findLegById(leg.getId()).getStatus();
			if (!leg.getStatus().equals(currentStatus))
				super.state(false, "status", "acme.validation.leg.change.status.no.published.flight");
		}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		SelectChoices statuses;
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		SelectChoices selectedAircrafts;
		Collection<Airport> airports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;

		statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircrafts = this.repository.findAllAircrafts();
		selectedAircrafts = SelectChoices.from(aircrafts, "aircraftLabel", leg.getAircraft());

		airports = this.repository.findAllAirports();
		selectedDepartureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		selectedArrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "departureAirport", "arrivalAirport", "aircraft", "draftMode");
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("status", LegStatus.ON_TIME);
		dataset.put("statuses", statuses);
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("departureAirports", selectedDepartureAirport);
		dataset.put("arrivalAirports", selectedArrivalAirport);
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("aircrafts", selectedAircrafts);
		dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
