
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
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		if (!super.getRequest().getData().isEmpty()) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer masterId = super.getRequest().getData("id", Integer.class);

			if (masterId != null) {
				Flight flight = this.repository.findFlightById(masterId);
				Manager manager = flight != null ? flight.getManager() : null;

				if (flight != null && flight.isDraftMode() && manager != null) {
					boolean tag = super.getRequest().hasData("tag");
					status = managerId == manager.getId() && super.getRequest().getPrincipal().hasRealm(manager) && tag;
				}
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
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		if (flight.getCost() != null) {
			boolean notAcceptedCurrency = flight.getCost().getCurrency().equals("EUR") || flight.getCost().getCurrency().equals("USD") || flight.getCost().getCurrency().equals("GBP");
			super.state(notAcceptedCurrency, "cost", "acme.validation.manager.flights.currency.not.valid");
		}
		boolean notPublished = flight.isDraftMode();
		super.state(notPublished, "draftMode", "acme.validation.flight.published.update");
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
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
