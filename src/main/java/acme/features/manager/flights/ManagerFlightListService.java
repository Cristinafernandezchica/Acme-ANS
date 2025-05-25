
package acme.features.manager.flights;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> flights;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flights = this.repository.findAllFlightsByManagerId(managerId);

		super.getBuffer().addData(flights);

	}

	@Override
	public void unbind(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");

		boolean hasLegs = !legs.isEmpty();
		dataset.put("originCity", hasLegs ? flight.originCity() : null);
		dataset.put("destinationCity", hasLegs ? flight.destinationCity() : null);
		dataset.put("scheduledDeparture", hasLegs ? flight.getScheduledDeparture() : null);
		dataset.put("scheduledArrival", hasLegs ? flight.getScheduledArrival() : null);

		super.getResponse().addData(dataset);
	}

}
