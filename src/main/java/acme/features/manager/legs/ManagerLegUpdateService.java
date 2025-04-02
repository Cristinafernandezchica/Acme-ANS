
package acme.features.manager.legs;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.Status;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Leg leg;
		Manager manager;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		masterId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(masterId);
		manager = leg == null ? null : leg.getFlight().getManager();
		status = leg != null && leg.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId();

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
		super.bindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "draftMode");
	}

	@Override
	public void validate(final Leg leg) {
		if (MomentHelper.isBefore(leg.getScheduledArrival(), leg.getScheduledDeparture()))
			super.state(false, "scheduledDeparture", "acme.validation.leg.departure.after.arrival.message");

		Date departureWithDelta = MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 5, ChronoUnit.MINUTES);
		if (MomentHelper.isBefore(leg.getScheduledArrival(), departureWithDelta))
			super.state(false, "scheduledArrival", "acme.validation.leg.departure.arrival.difference.message");

		if (leg.getDepartureAirport().equals(leg.getArrivalAirport()))
			super.state(false, "arrivalAirport", "acme.validation.leg.same.departure.arrival.airport");

		boolean notPublished = leg.isDraftMode();
		super.state(notPublished, "draftMode", "acme.validation.leg.published.update");
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
		Collection<Aircraft> activeAircrafts;
		Collection<Airport> airports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;

		statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircrafts = this.repository.findAllAircraftsByAirlineId(leg.getFlight().getAirline().getId());
		activeAircrafts = aircrafts.stream().filter(a -> a.getStatus().equals(Status.ACTIVE_SERVICE)).collect(Collectors.toList());
		selectedAircrafts = SelectChoices.from(activeAircrafts, "model", leg.getAircraft());

		airports = this.repository.findAllAirports();
		selectedDepartureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		selectedArrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "draftMode");
		dataset.put("flightNumber", leg.getFlightNumber());
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
