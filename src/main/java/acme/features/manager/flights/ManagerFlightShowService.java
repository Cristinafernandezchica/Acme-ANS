
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
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Integer masterId;
		Flight flight;
		Manager manager;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty()) {
			masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				flight = this.repository.findFlightById(masterId);
				manager = flight == null ? null : flight.getManager();
				status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && managerId == manager.getId();
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
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
		dataset.put("layovers", hasLegs ? flight.layovers() : null);

		if (hasLegs)
			dataset.put("flightId", flight.getId());

		super.getResponse().addData(dataset);
	}

}
