
package acme.features.manager.legs;

import java.util.Collection;
import java.util.stream.Collectors;

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
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Integer masterId;
		Leg leg;
		Manager manager;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (!super.getRequest().getData().isEmpty()) {
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				leg = this.repository.findLegById(masterId);
				manager = leg == null ? null : leg.getFlight().getManager();
				status = leg != null && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId();
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
		aircrafts = this.repository.findAllAircrafts();
		activeAircrafts = aircrafts.stream().filter(a -> a.getStatus().equals(Status.ACTIVE_SERVICE)).collect(Collectors.toList());
		selectedAircrafts = SelectChoices.from(activeAircrafts, "aircraftLabel", leg.getAircraft());

		airports = this.repository.findAllAirports();
		selectedDepartureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		selectedArrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "draftMode");
		if (leg.isDraftMode())
			dataset.put("status", leg.getStatus());
		else
			dataset.put("statuses", statuses);
		dataset.put("departureAirports", selectedDepartureAirport);
		dataset.put("arrivalAirports", selectedArrivalAirport);
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("aircrafts", selectedAircrafts);
		dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
