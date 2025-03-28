
package acme.features.manager.flights;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);
		manager = flight == null ? null : flight.getManager();
		status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);

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
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());

		boolean hasLegs = !legs.isEmpty();
		// Añadir como validación que todas las legs estén publicadas
		// cuando tenga la parte de legs

		super.state(hasLegs, "draftMode", "manager.flights.without.legs");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		// Collection<Leg> legs;
		// legs = this.repository.findLegsByFlightId(flight.getId());
		dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");
		/*
		 * if (!legs.isEmpty()) {
		 * dataset.put("originCity", flight.originCity());
		 * dataset.put("destinationCity", flight.destinationCity());
		 * dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		 * dataset.put("scheduledArrival", flight.getScheduledArrival());
		 * dataset.put("layovers", flight.layovers());
		 * } else {
		 * dataset.put("originCity", null);
		 * dataset.put("destinationCity", null);
		 * dataset.put("scheduledDeparture", null);
		 * dataset.put("scheduledArrival", null);
		 * dataset.put("layovers", null);
		 * }
		 */

		super.getResponse().addData(dataset);
	}

}
