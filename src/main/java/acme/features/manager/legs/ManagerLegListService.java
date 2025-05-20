
package acme.features.manager.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean showCreate = false;
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer flightId = null;
		boolean status = false;

		if (!super.getRequest().getData().isEmpty()) {
			flightId = super.getRequest().getData("flightId", Integer.class);
			if (flightId != null) {
				Flight flight = this.repository.findFlightById(flightId);
				showCreate = flight != null ? flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager()) : false;
				Manager manager = flight == null ? null : flight.getManager();
				status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId();

			}
		}

		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		Collection<Leg> legs;

		legs = this.repository.findAllLegsByFlightId(flightId);
		List<Leg> orderedByMomentLegs = new ArrayList<>(legs);

		Collections.sort(orderedByMomentLegs, Comparator.comparing(Leg::getScheduledDeparture));
		super.getResponse().addGlobal("flightId", flightId);
		super.getBuffer().addData(orderedByMomentLegs);

	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());

		super.getResponse().addData(dataset);

	}

}
