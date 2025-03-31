
package acme.features.manager.legs;

import java.util.Collection;

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
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Leg leg;
		Manager manager;

		masterId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(masterId);
		manager = leg == null ? null : leg.getFlight().getManager();
		status = leg != null && leg.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);

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

		Collection<Leg> allPublishedLegs = this.repository.findAllPublishedLegs();
		for (Leg publishedLeg : allPublishedLegs)
			if (publishedLeg.getAircraft().equals(leg.getAircraft())) {
				boolean overlap = MomentHelper.isBefore(publishedLeg.getScheduledDeparture(), leg.getScheduledArrival()) && MomentHelper.isAfter(publishedLeg.getScheduledArrival(), leg.getScheduledDeparture());
				if (overlap) {
					super.state(false, "aircraft", "acme.validation.leg.same.aircraft.message");
					break;
				}
			}

		Collection<Leg> flightPublishedLegs = this.repository.findAllPublishedLegsByFlightId(leg.getFlight().getId());
		for (Leg publishedLeg : flightPublishedLegs) {

			boolean overlap = MomentHelper.isBefore(publishedLeg.getScheduledDeparture(), leg.getScheduledArrival()) && MomentHelper.isAfter(publishedLeg.getScheduledArrival(), leg.getScheduledDeparture());
			// publishedLeg.getScheduledDeparture().before(leg.getScheduledArrival()) && publishedLeg.getScheduledArrival().after(leg.getScheduledDeparture());
			if (overlap) {
				super.state(false, "scheduledDeparture", "acme.validation.leg.overlap.message");
				super.state(false, "scheduledArrival", "acme.validation.leg.overlap.message");
				break;
			}

		}

		Collection<Leg> flightLegs = this.repository.findAllLegsByFlightId(leg.getFlight().getId());

		boolean publishedFlight = leg.getFlight().isDraftMode();
		boolean operativeAircraft = leg.getAircraft().getStatus().equals(Status.ACTIVE_SERVICE);

		super.state(operativeAircraft, "status", "acme.validation.leg.operative.aircraft.message");
		super.state(publishedFlight, "draftMode", "acme.validation.leg.flight.draftMode");
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
		Collection<Aircraft> activeAircrafts;
		Collection<Airport> airports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;

		statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircrafts = this.repository.findAllAircraftsByAirlineId(leg.getFlight().getAirline().getId());
		activeAircrafts = aircrafts.stream().filter(a -> a.getStatus().equals(Status.ACTIVE_SERVICE)).toList();
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
