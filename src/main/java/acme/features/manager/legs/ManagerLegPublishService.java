
package acme.features.manager.legs;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
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
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		final var request = super.getRequest();

		if (request.getData() != null && !request.getData().isEmpty()) {
			int managerId = request.getPrincipal().getActiveRealm().getId();
			Integer masterId = request.getData("id", Integer.class);

			if (masterId != null) {
				Leg leg = this.repository.findLegById(masterId);
				boolean hasFlightNumber = request.hasData("flightNumber");

				if (leg != null) {
					Manager manager = leg.getFlight().getManager();
					status = request.getPrincipal().hasRealm(manager) && managerId == manager.getId() && hasFlightNumber;

					if (status && "POST".equals(request.getMethod())) {
						// Departure Airport
						Integer depAirportId = request.getData("departureAirport", Integer.class);
						if (depAirportId == null)
							status = false;
						else if (depAirportId != 0 && this.repository.findAirportById(depAirportId) == null)
							status = false;

						// Arrival Airport
						if (status) {
							Integer arrAirportId = request.getData("arrivalAirport", Integer.class);
							if (arrAirportId == null)
								status = false;
							else if (arrAirportId != 0 && this.repository.findAirportById(arrAirportId) == null)
								status = false;
						}

						// Aircraft
						if (status) {
							Integer aircraftId = request.getData("aircraft", Integer.class);
							if (aircraftId == null)
								status = false;
							else if (aircraftId != 0 && this.repository.findAircraftById(aircraftId) == null)
								status = false;
						}

						// Leg Status
						if (status) {
							String legStatus = request.getData("status", String.class);
							if (legStatus != null && !legStatus.equals(LegStatus.ON_TIME.toString()))
								status = false;
						}
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
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {
		final Date currentMoment = MomentHelper.getCurrentMoment();
		final Date scheduledDeparture = leg.getScheduledDeparture();
		final Date scheduledArrival = leg.getScheduledArrival();

		// Validaciones de tiempo
		if (scheduledDeparture != null) {
			if (MomentHelper.isBefore(scheduledDeparture, currentMoment))
				super.state(false, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.past");

			if (scheduledArrival != null) {
				if (MomentHelper.isBefore(scheduledArrival, scheduledDeparture))
					super.state(false, "scheduledDeparture", "acme.validation.leg.departure.after.arrival.message");

				Date departureWithDelta = MomentHelper.deltaFromMoment(scheduledDeparture, 5, ChronoUnit.MINUTES);
				if (MomentHelper.isBefore(scheduledArrival, currentMoment))
					super.state(false, "scheduledArrival", "acme.validation.leg.scheduledArrival.past");

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

		// Validaciones de solapamiento
		if (scheduledDeparture != null && scheduledArrival != null) {
			Collection<Leg> allPublishedLegs = this.repository.findAllPublishedLegs();
			Aircraft aircraft = leg.getAircraft();
			boolean aircraftOverlap = false;

			for (Leg publishedLeg : allPublishedLegs)
				if (!aircraftOverlap && publishedLeg.getAircraft().equals(aircraft)) {
					aircraftOverlap = MomentHelper.isBefore(publishedLeg.getScheduledDeparture(), scheduledArrival) && MomentHelper.isAfter(publishedLeg.getScheduledArrival(), scheduledDeparture);
					if (aircraftOverlap)
						super.state(false, "aircraft", "acme.validation.leg.same.aircraft.message");
				}

			Collection<Leg> flightPublishedLegs = this.repository.findAllPublishedLegsByFlightId(leg.getFlight().getId());
			for (Leg publishedLeg : flightPublishedLegs) {
				boolean overlap = MomentHelper.isBefore(publishedLeg.getScheduledDeparture(), scheduledArrival) && MomentHelper.isAfter(publishedLeg.getScheduledArrival(), scheduledDeparture);
				if (overlap) {
					super.state(false, "scheduledDeparture", "acme.validation.leg.overlap.message");
					super.state(false, "scheduledArrival", "acme.validation.leg.overlap.message");
					break;
				}
			}
		}

		// Validaciones de aeronave
		if (leg.getAircraft() != null) {
			Aircraft aircraft = leg.getAircraft();
			super.state(aircraft.getStatus().equals(Status.ACTIVE_SERVICE), "aircraft", "acme.validation.leg.operative.aircraft.message");

			Airline airline = aircraft.getAirline();
			String flightNumber = leg.getFlightNumber();
			if (flightNumber.length() == 7 && !flightNumber.substring(0, 3).equals(airline.getIataCode())) {
				super.state(false, "flightNumber", "acme.validation.leg.invalid.iata.flightNumber");
				super.state(false, "flightNumber", "The airline's IATA code: " + airline.getIataCode());
			}
		}

		// Validación de vuelo publicado
		super.state(leg.getFlight().isDraftMode(), "*", "acme.validation.leg.flight.draftMode");

		// Validación de secuencia de legs
		if (leg.getFlight().getIndication() && departureAirport != null && arrivalAirport != null) {
			Collection<Leg> publishedLegs = this.repository.findAllPublishedLegsByFlightId(leg.getFlight().getId());
			publishedLegs.add(leg);
			List<Leg> orderedLegs = publishedLegs.stream().sorted(Comparator.comparing(Leg::getScheduledDeparture)).collect(Collectors.toList());

			int index = orderedLegs.indexOf(leg);
			if (index != -1) {
				// Validar leg anterior
				if (index > 0) {
					Leg previousLeg = orderedLegs.get(index - 1);
					if (!departureAirport.equals(previousLeg.getArrivalAirport()))
						super.state(false, "departureAirport", "acme.validation.leg.departureAirport");
				}

				// Validar siguiente leg
				if (index < orderedLegs.size() - 1) {
					Leg nextLeg = orderedLegs.get(index + 1);
					if (!arrivalAirport.equals(nextLeg.getDepartureAirport()))
						super.state(false, "arrivalAirport", "acme.validation.leg.arrivalAirport");
				}
			}
		}
	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
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

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "draftMode");
		dataset.put("flightNumber", leg.getFlightNumber());
		if (leg.isDraftMode())
			dataset.put("status", leg.getStatus());
		else
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
