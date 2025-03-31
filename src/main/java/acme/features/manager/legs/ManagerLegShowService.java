
package acme.features.manager.legs;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.Status;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

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
		status = super.getRequest().getPrincipal().hasRealm(manager) || leg != null;

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
	public void unbind(final Leg leg) {
		SelectChoices statuses;
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		SelectChoices selectedAircrafts;
		Collection<Aircraft> activeAircrafts;
		Collection<Airport> airports;
		Collection<Airport> departureAirports;
		Collection<Airport> arrivalAirports;
		SelectChoices selectedDepartureAirport;
		SelectChoices selectedArrivalAirport;
		int airlineId = leg.getFlight().getAirline().getId();

		statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircrafts = this.repository.findAllAircraftsByAirlineId(airlineId);
		activeAircrafts = aircrafts.stream().filter(a -> a.getStatus().equals(Status.ACTIVE_SERVICE)).toList();
		selectedAircrafts = SelectChoices.from(activeAircrafts, "model", leg.getAircraft());

		airports = this.repository.findAllAirports();
		departureAirports = airports.stream().filter(a -> !a.getIataCode().equals(leg.getArrivalAirport().getIataCode())).toList();
		arrivalAirports = airports.stream().filter(a -> !a.getIataCode().equals(leg.getDepartureAirport().getIataCode())).toList();
		selectedDepartureAirport = SelectChoices.from(departureAirports, "iataCode", leg.getDepartureAirport());
		selectedArrivalAirport = SelectChoices.from(arrivalAirports, "iataCode", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "draftMode");
		dataset.put("statuses", statuses);
		dataset.put("departureAirports", selectedDepartureAirport);
		dataset.put("arrivalAirports", selectedArrivalAirport);
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("aircrafts", selectedAircrafts);
		dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
